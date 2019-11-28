"""
This is the people module and supports all the REST actions for the
PEOPLe data-structures
"""


#System Modules
from datetime import datetime

# 3rd Party Modules
from flask import make_response, abort

def get_timestamp():
	return datetime.now().strftime(("%Y-%m-%d %H:%M:%S"))

# Data to serve with our API
PEOPLE = {
    "Farrell": {
        "fname": "Doug",
        "lname": "Farrell",
        "timestamp": get_timestamp()
    },
    "Brockman": {
        "fname": "Kent",
        "lname": "Brockman",
        "timestamp": get_timestamp()
    },
    "Easter": {
        "fname": "Bunny",
        "lname": "Easter",
        "timestamp": get_timestamp()
    }
}

# Create a handler for our read (GET) people
def read():
    """
    This function responds to a request for /api/people
    with the complete lists of people

    :return:        sorted list of people
    """
    # Create the list of people from our data
    return [PEOPLE[key] for key in sorted(PEOPLE.keys())]

def create(person):
    """
    This function creates a new person in the people structure
    based on the passed in person data

    :param person:  person to create in people structure
    :return:        201 on success, 406 on person exists
    """    
    print(person)
    lastName = person.get("lname", None)
    firstName = person.get("fname", None)

    # Does the person already exist?
    if lastName not in PEOPLE and firstName is not None: 
        PEOPLE[lastName] = {
            "lname": lastName,
            "fname": firstName,
            "timestamp": get_timestamp(),
        }
        return make_response(
            "{lname} successfully created".format(lname=lastName), 201
        )

    # Otherwise, they exist and that's an error
    else:
        abort(
            406,
            "Person with the last name {lname} already exist".format(lname=lastName),
        )
















