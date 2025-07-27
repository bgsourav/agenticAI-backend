from fastapi import FastAPI
from fastapi.responses import StreamingResponse, JSONResponse
import os
import asyncio
import json
import tempfile
from subscribingChatter import fetch_chatter_message
from subscribingNetwork import fetch_network_message
from subscribingPublic import fetch_public_message
from fastapi.encoders import jsonable_encoder

app = FastAPI()

REASONING_ENGINE_ID = "5100986284973752320"
PROJECT_ID = "pulse-bengaluru-agents"
LOCATION = "us-central1"
BEARER_TOKEN = "ya29.a0AS3H6NyyrkFmBKI0YuPhk9rP42Uj8WUrREBLjR7gCBoDTTOupR7shKEaB0OI3J6hwE7p0Jp8q6CHJIshNI0CcaZX25xpzoO9bdUmxgeyKrxGIG67eODPPgrcoFi6BIoEQMDtM-K2pDRmikEgZNT7ZLzoOEknjDyW3mcqgIXhm5MMiAaCgYKAYMSARESFQHGX2MisfG2TME4ivV9sZiDO1H7dg0181"

def safe_convert(obj):
    try:
        json.dumps(obj)
        return obj
    except:
        try:
            return jsonable_encoder(obj)
        except:
            return []

def get_data():
    try:
        chatter_data = fetch_chatter_message()
        # network_data = fetch_network_message()
        public_data = fetch_public_message()
        
        chatter_data = safe_convert(chatter_data) if chatter_data else []
        public_data = safe_convert(public_data) if public_data else []
        
        # if isinstance(network_data, dict):
        #     network_history = network_data.get("history", [])
        #     network_latest = network_data.get("Latest", {})
        # elif isinstance(network_data, list):
        #     network_history = network_data
        #     network_latest = {}
        # else:
        #     network_history = []
        #     network_latest = {}
        
        message_data = {
            # "network_quality_agent": {
            #     "Area": "HSRLayout",
            #     "history": network_history,
            #     "Latest": network_latest
            # },
            "radio_charter_data": chatter_data,
            "social_media_data": public_data
        }
        
        return safe_convert(message_data)
    except:
        return {"network_quality_agent": {"Area": "HSRLayout", "history": [], "Latest": {}}, "radio_charter_data": [], "social_media_data": []}

async def exec_curl(message_data):
    temp_file_path = None
    try:
        request_body = {
            "class_method": "stream_query",
            "input": {
                "user_id": "USER_ID",
                "session_id": "3474890501109317632",
                "message": json.dumps(message_data)
            }
        }
        
        with tempfile.NamedTemporaryFile(mode='w', suffix='.json', delete=False) as temp_file:
            json.dump(request_body, temp_file)
            temp_file_path = temp_file.name
        
        process = await asyncio.create_subprocess_exec(
            'curl',
            '-H', f'Authorization: Bearer {BEARER_TOKEN}',
            '-H', 'Content-Type: application/json',
            f'https://{LOCATION}-aiplatform.googleapis.com/v1/projects/{PROJECT_ID}/locations/{LOCATION}/reasoningEngines/{REASONING_ENGINE_ID}:streamQuery?alt=sse',
            '-d', f'@{temp_file_path}',
            stdout=asyncio.subprocess.PIPE,
            stderr=asyncio.subprocess.PIPE
        )
        
        stdout, stderr = await process.communicate()
        
        if process.returncode != 0:
            return f"Error: {stderr.decode()}"
        
        return stdout.decode()
        
    except Exception as e:
        return f"Error: {str(e)}"
    finally:
        if temp_file_path and os.path.exists(temp_file_path):
            try:
                os.unlink(temp_file_path)
            except:
                pass

def parse_response(response_data):
    try:
        content = response_data.get("content", {})
        parts = content.get("parts", [])
        
        result = {
            "radio_charter_analysis": None,
            "social_media_analysis": None,
            "network_quality_analysis": None
        }
        
        for part in parts:
            if "function_call" in part:
                func_call = part["function_call"]
                func_name = func_call.get("name", "")
                args = func_call.get("args", {})
                
                if func_name == "radio_charter_analyst":
                    try:
                        result["radio_charter_analysis"] = json.loads(args.get("request", "{}"))
                    except:
                        pass
                elif func_name == "social_media_analyst":
                    try:
                        result["social_media_analysis"] = json.loads(args.get("request", "{}"))
                    except:
                        pass
        
        return result
    except:
        return {"error": "Parse failed"}

@app.get("/query")
async def query():
    try:
        message_data = get_data()
        curl_output = await exec_curl(message_data)
        
        if curl_output.startswith("Error:"):
            return JSONResponse(status_code=500, content={"error": curl_output})
        
        best_result = None
        for line in curl_output.split('\n'):
            line = line.strip()
            if line.startswith('data: '):
                try:
                    response_json = json.loads(line[6:])
                    parsed = parse_response(response_json)
                    if any(v for v in parsed.values() if v is not None):
                        best_result = parsed
                except:
                    continue
        
        if best_result:
            return JSONResponse(content=best_result)
        else:
            return JSONResponse(content={"raw_output": curl_output})
            
    except Exception as e:
        return JSONResponse(status_code=500, content={"error": str(e)})

@app.get("/stream-query")
async def stream_query():
    try:
        message_data = get_data()
        
        async def stream_response():
            try:
                yield f"data: {json.dumps({'status': 'starting'})}\n\n"
                
                curl_output = await exec_curl(message_data)
                
                if curl_output.startswith("Error:"):
                    yield f"data: {json.dumps({'error': curl_output})}\n\n"
                    return
                
                for line in curl_output.split('\n'):
                    line = line.strip()
                    if not line:
                        continue
                        
                    if line.startswith('data: '):
                        try:
                            response_json = json.loads(line[6:])
                            parsed = parse_response(response_json)
                            if any(v for v in parsed.values() if v is not None):
                                yield f"data: {json.dumps(parsed)}\n\n"
                            else:
                                yield f"data: {json.dumps({'raw': line[6:]})}\n\n"
                        except:
                            yield f"data: {json.dumps({'raw': line[6:]})}\n\n"
                    else:
                        yield f"data: {json.dumps({'info': line})}\n\n"
                        
            except Exception as e:
                yield f"data: {json.dumps({'error': str(e)})}\n\n"
        
        return StreamingResponse(stream_response(), media_type="text/event-stream")
        
    except Exception as e:
        return JSONResponse(status_code=500, content={"error": str(e)})

@app.get("/health")
async def health():
    return {"status": "ok"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)