import os
from google.cloud import pubsub_v1
import json

credentials_path = '/Users/souravbg/Downloads/Bengaluru Agents Data.json'
os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = credentials_path


def fetch_network_message():
    subscriber = pubsub_v1.SubscriberClient()
    subscription_path = 'projects/pulse-bengaluru-agents/subscriptions/network-agent-subscription-sub'
    try:
        response = subscriber.pull(
            request={"subscription": subscription_path, "max_messages": 1, "return_immediately": True}
        )
        if response.received_messages:
            received_message = response.received_messages[0]
            message_data = received_message.message.data.decode("utf-8")
            ack_id = received_message.ack_id
            subscriber.acknowledge(request={"subscription": subscription_path, "ack_ids": [ack_id]})
            return json.loads(message_data)
        else:
            return None
    except Exception as e:
        return None
    finally:
        subscriber.close()
