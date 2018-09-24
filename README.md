<img src="https://raw.githubusercontent.com/Brytecore/brytescore.js/master/examples/lead-booster-analytics.png" width="400" height="98" alt="Lead Booster Analytics">

# brytescore-android

brytescore-android is the open-source Android SDK that connects your website with the Brytescore API. The
Brytescore API allows you to track your users' behavior and score their engagement.

## Example

To run the example project, clone the repo, and build `app`.

## Installation

To install it, simply add the brytescore folder to your project's directory,
then add the following line to your app's build.gradle:

```ruby
compile project(path: ':brytescore')
```

## Methods

### Initialization

Sets the API key.
Generates a new unique session ID.
Retrieves the saved user ID, if any.

- parameter {string} The API key.

```java
    brytescore = new Brytescore(getApplicationContext(), "<api-key>");
```

### getAPIKey

Returns the current API key

- returns: The current API key

```java
    String apiKey = brytescore.getAPIKey();
```

### load

Function to load json packages.

- parameter {string} The name of the package.

```java
    brytescore.load("realestate");
```

### devMode

Sets dev mode.
Logs events to the console instead of sending to the API.
Turning on dev mode automatically triggers debug mode.

- parameter enabled: If true, then dev mode is enabled.

```java
    brytescore.devMode(devMode);
```

### debugMode

Sets debug mode.
Log events are suppressed when debug mode is off.

- parameter enabled: If true, then debug mode is enabled.

```java
    brytescore.debugMode(debugMode);
```

### impersonationMode

Sets impersonation mode.
Bypasses sending information to the API when impersonating another user.

- parameter enabled: If true, then impersonation mode is enabled.

```java
    brytescore.impersonationMode(impersonationMode);
```

### validationMode

Sets validation mode.
Adds a validation attribute to the data for all API calls.

- parameter enabled: If true, then validation mode is enabled.

```java
    brytescore.validationMode(validationMode);
```

### brytescore

Start tracking a property specific to a loaded package.

- parameter property: The property name
- parameter data: The property tracking data

```java
    brytescore.brytescore("<property-name>", propertyData);
```

### pageView

Start a pageView.

- parameter data: The pageView data.
- data.isImpersonating
- data.pageUrl
- data.pageTitle
- data.referrer

```java
    brytescore.pageView(pageViewData);
```

### registeredAccount

Sends a new account registration event.

- parameter data: The registration data.
- data.isImpersonating
- data.userAccount.id

```java
    brytescore.registeredAccount(registeredAccountData);
```

### submittedForm

Sends a submittedForm event.

- parameter data: The chat data.
- data.isImpersonating

```java
    brytescore.submittedForm(submittedFormData);
```

### startedChat

Sends a startedChat event.

- parameter data: The form data.
- data.isImpersonating

```java
    brytescore.startedChat(startedChatData);
```

### updatedUserInfo

Updates a user's account information.

- parameter data: The account data.

```java
    brytescore.updatedUserInfo(updatedUserInfoData);
```

### authenticated

Sends a user authentication event.

- parameter data: The authentication data.
- data.isImpersonating
- data.userAccount
- data.userAccount.id

```java
    brytescore.authenticated(authenticatedData);
```

### killSession

Kills the session.

```java
    brytescore.killSession();
```
