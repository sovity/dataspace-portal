<!-- PROJECT LOGO -->
<br />
<div align="center">
<a href="https://github.com/sovity/authority-portal">
<img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
</a>

<h3 align="center">Data Space Portal</h3>
<p align="center" style="padding-bottom:16px">
Manage your Dataspace
<br />
<a href="https://github.com/sovity/authority-portal/issues/new?assignees=&labels=kind%2Fbug&projects=&template=bug_report.yaml">Report Bug</a>
·
<a href="https://github.com/sovity/authority-portal/issues/new?template=feature_request.md">Request Feature</a>
</p>
</div>

# List of features

### Quick access

- [Global Features](#global)
- [Participant User](#participant-user)
- [Participant Curator / Key User](#participant-curator--key-user)
- [Participant Admin](#participant-admin)
- [Authority User](#authority-user)
- [Authority Admin](#authority-admin)
- [Service Partner Admin](#service-partner-admin)
- [Operator Admin](#operator-admin)

## Global

### Features

#### Environment Switcher
- You can switch between available deployment environments in the switcher at the top of the page
- Every deployment environment has its own central components and its own connectors, only organizations and users are shared
  - Because the connectors are separated, every environment has its own catalog with its own data offers

#### Landing page
- When not logged in, you can see a page where you can choose between "Login" and "Register Organization"
  - The former redirects you to the Keycloak Login page
  - The latter redirects you to the registration format

#### Login page
- The login page is an external Keycloak page
  - You can login or reset your password

#### Registration page
- After filling all fields, the account is created in Keycloak
  - Should an account for the provided email already exist in Keycloak, there are 2 options as to what happens next
    - If this email is also already present in the AP database, the registration is rejected with an error stating that the email is already used
    - If this email is **not** present in the AP database, the user in Keycloak is deleted and recreated

#### Onboarding
- If you were invited and are logging in for the first time, you will be asked to provide additional information
  - If your organization was just invited, you need to provide both information about you and your organization
  - If you were invited to an organization, you only need to provide your user information

#### Welcome page
- After successfully onboarding, you briefly see a page with a welcome text
- A similar page is displayed when you are the first user registering and the process is successful

#### Pending/Rejected page
- If you are registering on your own but were not invited, your registration needs to be approved by the dataspace authority
  - As long as you wait for approval, you will see a page stating that you need to wait
- If your registration is rejected, you will see a rejection page instead

## Participant User

A Participant User is the basic user with read-only permissions

### Features

#### Data Catalog
- You can view all public data offers in the dataspace in an overview
  - as a list
  - in a grid
- You can click on any data offer to see details, like the connector endpoint
- You can search use the universal search to find a data offer using any attribute
- You can use (dynamically determined) available filters to filter offers by organizations for example

#### Status Dashboard
The availability of this feature is dependent on the Portal's configuration determined by your Dataspace Authority
- You can see the historical uptime of components connected to the Portal (DAPS, Crawler)
- You can see an overview of how many connectors are online/offline
- You can export following reports in CSV format
  - Information about all registered connectors
  - Information about all public data offers in the dataspace
  - Historical information about component downtimes and system stability

#### "My Organization"
- You can view details about your organization
- You can switch to "My Profile" and "Users and Roles" here. Those pages will be listed separately here

#### "My Profile"
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

## Participant Curator / Key User

A Participant Curator (Key User) has management rights for connectors in their organization

### Features

The Participant Curator has access to all functions the **Participant User** has.
Additional features are listed below.

#### "My Connectors"
- You can register self-hosted connectors for your organization
- You can request a ready-to-go CaaS from sovity
  - This availability of this function depends on your Dataspace's configuration
- You can delete registered connectors in your organization

## Participant Admin

A Participant Admin is the highest role in an organization and has broad management rights

### Features

The Participant Admin has access to all functions the **Participant Curator** has.
Additional features are listed below.

#### "My Organization"
- You can edit your organization's details, except for the following
  - Organization Name
  - Legal identification number

#### "Users and Roles"
- You can invite new users to your organization
- You can deactivate/reactivate users in your organization
  - Keep in mind that deactivation is not the same as deletion. Only the user themselves can delete their account
- You can change users' roles in your organization

## Authority User

An Authority User is an application role, it has rights and features in addition to the ones provided by the user's organization role.
It is the basic role for the Dataspace Authority

### Features

The Authority User has access to all functions their **Organization Role** provides.
Additional features provided by this application role are listed below.

#### Status Dashboard
The availability of this feature is dependent on the Portal's configuration determined by your Dataspace Authority
- You can export a CSV report with information about all users and their roles

#### Authority Section - Organizations
- You can view all registered organizations in the dataspace and their details
  - You can filter the list by registration status
  - The list/details show the number of users, connectors and data offers for each organization
- You can invite new organizations to join the dataspace
- You can accept/reject pending self-registrations of organizations
- You can view the member list of any organization
- You can deactivate & delete users from any organization

#### Authority Section - All Connectors
- You can view all connectors registered in the dataspace, as well as their details

## Authority Admin

An Authority Admin is an application role, it has rights and features in addition to the ones provided by the user's organization role.
It is the default role for the very first registered user and is the dataspace administrator.

### Features

The Authority Admin has access to all functions their **Organization Role** provides.
In addition to the features listed below, they have also access to everything the **Authority User** does

#### Users and Roles
- You can assign other users application roles
  - Authority User
  - Authority Admin
  - Service Partner Admin
  - Operator Admin

#### Authority Section - Organizations
- You can delete organizations

## Service Partner Admin

Service Partner Admin is an application role, it has rights and features in addition to the ones provided by the user's organization role.
It is the role for service providers and enables them to provide connectors for other participants

### Features

The Service Partner Admin has access to all functions their **Organization Role** provides.
Additional features are listed below.

#### Service Partner Section - Provided Connectors
- You can view all connectors your organization provides to other participants
- You can register connectors for other organizations
  - This is a 2-step process to account for the Customer Portal flow when provisioning connectors
  - You can use a JWKS URL instead of a certificate, shortening the process
- You can delete connectors you have provided for other organizations

## Operator Admin

Operator Admin is an application role, it has rights and features in addition to the ones provided by the user's organization role.
It is the role for service providers and enables them to provide connectors for other participants

### Features

The Operator Admin has access to all functions their **Organization Role** provides.
Additional features are listed below.

#### Operator Section - All Connectors
- You can view all connectors registered in the dataspace
- You can delete any connector

#### Operator Section - Central components
- You can view all registered central components
- You can register central components (e.g. Catalog Crawler) to the portal
- You can delete registered components
