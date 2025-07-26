from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse
import httpx
import json

from subscribingChatter import fetch_chatter_message
from subscribingNetwork import fetch_network_message
from subscribingPublic import fetch_public_message

app = FastAPI()

EXTERNAL_API_URL = "https://us-central1-aiplatform.googleapis.com/v1/projects/pulse-bengaluru-agents/locations/us-central1/reasoningEngines/5100986284973752320:streamQuery?alt=sse"

@app.get("/getAlerts")
async def get_alerts():
    try:
        # Fetch data from subscriber streams
        chatter_data = fetch_chatter_message()
        network_data = fetch_network_message()
        public_data = fetch_public_message()

        # Combine the data into a single dictionary
        combined_data = {
            "radio_chatter_data": chatter_data,
            "network_quality_data": network_data,
            "social_media_data": public_data
        }

        # Extract authorization header from the incoming request
        auth_header = ""
    
        # Construct the payload for the external API call
        payload = {
            "class_method": "stream_query",
            "input": {
                "user_id": "USER_ID", # You might want to get this from the incoming request or configuration
                "session_id": "3474890501109317632", # You might want to get this from the incoming request or generate it
                "message": json.dumps(combined_data) # Convert combined data to a JSON string
            }
        }

        headers = {
            "Authorization": auth_header,
            "Content-Type": "application/json"
        }

        async with httpx.AsyncClient() as client:
            response = await client.post(EXTERNAL_API_URL, headers=headers, json=payload)
            response.raise_for_status()  # Raise an exception for bad status codes (4xx or 5xx)

        return JSONResponse(status_code=response.status_code, content=response.json())

    except httpx.HTTPStatusError as e:
        print(f"HTTP error occurred: {e.response.status_code} - {e.response.text}")
        return JSONResponse(status_code=e.response.status_code, content={"message": f"External API error: {e.response.text}"})
    except Exception as e:
        print(f"An error occurred: {e}")
        return JSONResponse(status_code=500, content={"message": f"Internal server error: {e}"})

