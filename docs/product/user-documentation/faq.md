---
icon: message-question
---

## Welcome to our Frequently Asked Questions (FAQ) collection!

This section is designed to provide quick and clear answers to the most common queries we receive. Whether you're looking for information on our products or services or need help with troubleshooting, you'll find the answers here. If you can't find the answer to your question, don't hesitate to open a [dicussion](https://github.com/sovity/dataspace-portal/discussions).

### How does the availability check for the connectors work?

The connector online status is determined and set by the catalog crawler. If the crawler can fetch the catalog, it will set the connector to ONLINE, on a failure to OFFLINE. Keep in mind that the catalog crawler only crawls connectors in the same environment as the crawler itself.

---
### Can a user with the same email address be registered in multiple organizations?

Currently, this feature is not supported in DSPortal. We will consider this enhancement in a future iteration of the DSPortal when we have gathered sufficient market evidence for prioritizing this feature.

Workaround Solution: If the user's email address is a Microsoft or Google email address, they can use the "plus addressing" feature that Microsoft and Google offer to create unique variations of their email address while still being linked to the same inbox. 

For example, if a user's primary email is: `firstname.lastname@abc.com`, they can create different email variations for different organizations, such as:
- `firstname.lastname+fictionalorganization1admin@abc.com`
- `firstname.lastname+fictionalorganization1user@abc.com`

These addresses will all be routed to `firstname.lastname@abc.com`, allowing the user to register multiple personas while maintaining a single email inbox.

---
### Is EDC-API control available for a connector provisioned via the "Request CaaS" functionality?If it is available, how can I check the API key?

Yes EDC-API control is for a connector provisioned via the "Request CaaS" functionality. To request for the API key,
- Click on "Support" button on the left navigation panel
- Write your request. **For example: **"Please provide me with the API Key for "Connector Name":
- Please provide us with the relevant Connector Name (You can find this information on "My Connectors" page)

---
### Organization- and User-Registration: How should I fill in required fields if certain data is not available?

In cases where specific required information is not available, you can use a placeholder value. For example, if a "Legal ID Number" is not applicable in your country or you are intending to set up demo organizations, you may enter "00000" or another suitable placeholder. This principle applies to all required fields in general, including cases when registering individuals instead of organizations. The key requirement is that the provided data allows for proper validation and approval by the Data Space Authority in the concrete use-case.
