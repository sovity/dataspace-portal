---
icon: hand-wave
---

# General Overview

{% hint style="info" %} This component was previously known as Authority Portal (AP) and has been renamed to Data Space Portal (DSPortal). The functionality remains unchanged. {% endhint %}

This guide provides a general overview of the main sections and features of the portal, helping you navigate and understand the various components available.

## Landing Page

When not logged in, you can see a page where you can choose between "Login" and "Register Organization"
- "Login" redirects you to the Keycloak Login page
- "Register Organization" redirects you to the registration form

## Login Page
The login page is powered by Keycloak page adapted to sovity's infrastructure. You can login or reset your password.

## Header Navigation Overview

Assuming successful login, let's take a look at the features of the DSPortal.

### Environment Switcher

- You can switch between available deployment environments in the switcher at the top right of the page.  
- Every deployment environment has its own central components and its own connectors. Only organizations and users are shared.
  - Because the connectors are separated, every environment has its own catalog with its own data offers.

## Left Panel Navigation Overview

On the left side of the UI, the Data Space Portal is structured into six main sections, each serving a distinct purpose. You can access each section from the left navigation panel. Below is a brief description of each:

### 1. Home
The Home section is your landing page within the Data Space Portal if no direct links are used. It provides an overview of the system with the Dashboard and quick access to the Data Catalog.

#### Data Catalog
- You can view all public data offers in the dataspace in an overview
  - as a list
  - in a grid
- You can click on any data offer to see details, like the connector endpoint
- You can search use the universal search to find a data offer using any attribute
- You can use (dynamically determined) available filters to filter offers by organizations for example

#### Dashboard
{% hint style="info" %} The availability of this feature is dependent on the Portal's configuration determined by your Dataspace Authority {% endhint %}

- You can see the historical uptime of components connected to the Portal (DAPS, Crawler)
- You can see an overview of how many connectors are online/offline
- You can export following reports in CSV format
  - Information about all registered connectors
  - Information about all public data offers in the dataspace
  - Historical information about component downtimes and system stability

### 2. Your Organization
This section is designed to manage and view the organization participating in the data space. Here, you can see detailed information about your organization, your data offers and the connectors of your organization.

#### My Organization
This section allows users to view and manage their organization’s details. It provides a centralized interface to view and update organizational information, manage users, and configure settings.
- You can switch to "My Profile" and "Users and Roles" tabs as needed.
- About "My Profile" tab:
  - You can view details about your own profile
  - You can edit following user information in your account
     - First Name
     - Last Name
     - Job Title
     - Email
        - When changing your email, you will need to confirm and use it on your next login attempt
     - Phone Number
   - You can delete your profile, there are some conditions which are as follows
     - All references to you (field "invited by" etc.) will be removed
     - All connectors you have registered will be transferred to your organization's admin
     - If you are the only admin in your organization, you must be the only member to delete your account
        - The organization will also be deleted, all connectors registered will also be removed
  - "Users and Roles"
    - You can view all members of your organization and display their profile details
  - "My Data Offers"
    - This is a shortcut to the Data Catalog with pre-applied filters to display your organization's data offers
    - "My Connectors"
      - You can view your organization's registered connectors and display their details

#### My Data Offers
This section provides a filtered view of the Data Catalog, allowing to specifically browse the data offers created by your organization. This enables efficient tracking and oversight of the organization's data offers within the dataspace, which are viewable for every participant.

#### My Connectors
This section provides an overview of all connectors of your organization that have been set up, enabling to monitor their status and ensure proper integration with the dataspace. It also allows for creating and adding new connectors.

### 3. Operator Section
This section is dedicated to the operators of a data space who deploy and manage the central components of the data space in the infrastructure and manage the different environments of the Data Space.

#### All Connectors
As an operator, this area provides you with an overview of the different connectors, their type and status.

#### Central Components
As an operator, this area provides you with an overview of the central components in the data space you provide and enables you to provide additional central components. 

**To learn more about the Operator Admin role, please read the section [Application role: Operator Admin](Manage%20Data%20Space%20components.md#application-role-operator-admin).**

### 4. Service Partner Section
The Service Partner section is dedicated to service partners of the data space, who provide connectors for other data space participants by deploying and registering these connectors for other entities within the data space.

#### Provided Connectors
This section enables the service parnter with direct access to all provided connectors and their status. 

**To learn more about the Service Partner Admin role, please read the section [Application role: Service Partner Admin](Manage%20Data%20Space%20components.md#application-role-service-partner-admin).**

### 5. Authority Section
This section is intended for administrators of the data space and includes advanced features for managing the Data Space Portal and the organizations in the data space.

#### Organizations
As a data space authority, all organizations registered in the data space can be viewed and managed here. New organizations can also be invited.

#### All Connectors
This is a central point for viewing all connectors and their status and further details. 

**To learn more about the Authority section, please read the documentation: [Authority Section](Authority%20Section.md).**

### 6. Support
The Support section is intended to assist users with any issues or inquiries they might have while using the Data Space Portal. Please note that this section requires integration with a support system of your choice to be fully functional.
