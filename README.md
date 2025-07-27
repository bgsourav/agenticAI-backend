# Agentic AI Backend

This repository contains the backend services for an Agentic AI application, built with FastAPI. It integrates with Google Cloud's AI Platform to process and analyze data from various sources like radio chatter, social media, and network quality.

## Technologies Used

*   **Python**: The core programming language.
*   **FastAPI**: A modern, fast (high-performance) web framework for building APIs with Python 3.7+.
*   **Uvicorn**: An ASGI server for running FastAPI applications.
*   **Google Cloud AI Platform**: Used for reasoning engine capabilities.
*   **`curl`**: Command-line tool used for making HTTP requests to the AI Platform.

## Setup Instructions

Follow these steps to set up and run the backend application locally.

### Prerequisites

*   Python 3.7+
*   `pip` (Python package installer)
*   Access to Google Cloud Platform with a configured AI Platform Reasoning Engine.
*   `curl` installed on your system.

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/agenticAI-backend.git
cd agenticAI-backend
```

### 2. Install Dependencies

Install the required Python packages using `pip`:

```bash
pip install -r requirements.txt
```

### 3. Configure Environment Variables

The application requires several environment variables for authenticating and interacting with the Google Cloud AI Platform. You need to set these before running the application.

*   `REASONING_ENGINE_ID`: The ID of your Google Cloud AI Platform Reasoning Engine.
*   `PROJECT_ID`: Your Google Cloud Project ID.
*   `LOCATION`: The Google Cloud region where your Reasoning Engine is deployed (e.g., `us-central1`).
*   `BEARER_TOKEN`: A valid OAuth 2.0 access token for authentication with Google Cloud APIs. This token typically has a short lifespan and needs to be refreshed. For local development, you might generate one using `gcloud auth print-access-token`.

You can set these variables in your shell or create a `.env` file and load them using a library like `python-dotenv` (though the current setup expects them to be in the environment).

Example (for Linux/macOS):

```bash
export REASONING_ENGINE_ID="your_reasoning_engine_id"
export PROJECT_ID="your_project_id"
export LOCATION="your_gcp_region"
export BEARER_TOKEN="your_bearer_token" # Get this using 'gcloud auth print-access-token'
```

### 4. Run the Application

Once the dependencies are installed and environment variables are set, you can run the FastAPI application using Uvicorn:

```bash
uvicorn main:app --host 0.0.0.0 --port 8000
```

This will start the server, typically accessible at `http://0.0.0.0:8000`.

## API Endpoints

The backend exposes the following main endpoints:

*   `/query`: A GET endpoint that processes data and returns a single JSON response from the AI Platform.
*   `/stream-query`: A GET endpoint that streams responses from the AI Platform as Server-Sent Events (SSE).
*   `/health`: A simple GET endpoint to check the health status of the application.

