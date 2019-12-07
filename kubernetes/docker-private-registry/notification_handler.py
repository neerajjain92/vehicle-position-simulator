# 3rd Party Modules
from flask import make_response, abort
import requests
import json

# Handling notification from the private registry
events = []

def handle(notification):
	events = notification.get("events")

	# Getting the first event
	firstEvent = events[0]

	if (firstEvent.get("action") == "push"):
		target = firstEvent.get("target")
		if(target.get("mediaType") == "application/vnd.docker.distribution.manifest.v2+json"):

			# Lets send notification to Slack channel
			url = "https://hooks.slack.com/services/PUT_YOUR_SLACK_URL_HERE"
			# Image Name
			image = target.get("repository")
			tag = target.get("tag")
			imageURL = firstEvent.get("request").get("host") + "/" + image + ":" + tag
			payload = {
				'text': 'New Image pushed to Private Docker Registry \n Image Name ==> {0} Tag ==> {1} \n Image URL ==> {2}'.format(image, tag, imageURL),
				'icon_emoji': ':penguin:'
			}
			r = requests.post(url, data=json.dumps(payload))

	return make_response(
            "Notification Sent Successfully to Slack....", 201
        )
