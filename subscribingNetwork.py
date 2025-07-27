import os
from google.cloud import pubsub_v1
import json

credentials_path = '/Users/souravbg/Downloads/Bengaluru Agents Data.json'
os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = credentials_path


def fetch_network_message():
    subscriber = pubsub_v1.SubscriberClient()
    subscription_path = 'projects/pulse-bengaluru-agents/subscriptions/network-agent-subscription-sub'

    response = subscriber.pull(
        request={
            "subscription": subscription_path,
            "max_messages": 10,
        },
        timeout=5
    )

    messages = []
    ack_ids = []
    for received_message in response.received_messages:
        msg_data = {
            "data": received_message.message.data.decode('utf-8') if hasattr(received_message.message.data, 'decode') else str(received_message.message.data),
            "attributes": received_message.message.attributes,
        }
        messages.append(msg_data)
        ack_ids.append(received_message.ack_id)

    if ack_ids:
        subscriber.acknowledge(request={"subscription": subscription_path, "ack_ids": ack_ids})

    # If no messages, just return an empty list
    return messages