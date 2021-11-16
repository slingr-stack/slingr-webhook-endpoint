---
title: SLINGR Webhook endpoint
keywords: 
last_updated: March 1, 2018
tags: []
summary: "Detailed description of the API of the SLINGR Webhook endpoint."
---

## Overview

The SLINGR endpoint webhook allows notifying to other applications about events happening into the running SLINGR application.

## Configuration

Before using the endpoint you need to create a user in the SLINGR app you want to propagate events.

This endpoint allows to set multiple targets to notify about events happening on SLINGR App.

### Notifications for all entities

This indicator means that the endpoint will notify about events happening in records of all entities of application. Be aware 
to leave this value in `true` since an overload of notifications could happen. 

### Events to notify

It is only available when the previous field is set as `true`. This field specifies the type of events to notify, by default 
it considers all events, but developer can choose some of them.

#### Create

Will notify every time a record is created. The data sent will include `record` information. 

#### Update

Will notify every time a record is changed. The data sent will include `record` and `oldRecord` information. Keep in
mind this event will notify to listeners every time record is saved, there are changes or not, so be aware when enabling
this options and records are saved several times in short time intervals. 

#### Delete

Will notify every time a record is deleted. The data sent will include `oldRecord` information. 

#### Actions

Will notify every time any action is executed over a record. The data sent will include `record` and `oldRecord` information. 

### Entities to notify events

It is only available when the previous field is set as `false`. Allows to specify a list of entities and events to notify.

#### Entity

Entity into the SLINGR app whose records manipulation will trigger events propagation into the endpoint.

#### Events to notify

Same as the configured for all entities, but this only applies for the entity in previous field.

### Webhooks type

Indicates the type of webhooks that endpoint will manage. `Fixed` means that a list of URL's and their tokens will be set statically.
In the other hand, `Dynamic` allows to use records of an entity as webhook information.

#### Webhooks

List of static webhooks information. Available when `Webhooks type` is `Fixed`.

##### URL

URL of target webhook to call when event comes.

##### Verification token

Token to pass as header to webhook target when event arrives.

#### Webhooks entity

Entity into SLINGR app whose records contains webhook target information.

#### Webhooks URL field

Field into records of previous entity that contains the URL of target webhook.

#### Webhooks token field

Field into records of previous entity that contains the token to sent to target webhook.

## Javascript API

For now, this endpoint contains only one function to propagete event to webhooks.

### Publish event

This method sent event information to all configured webhooks


```js
var endpoint = app.endpoints.slingrWebhook;
var config = endpoint._configuration;
if (config.webhooksType == 'fixed') {
    //fixed list of webhooks
    for (var i in config.webhooks) {
        endpoint.publishEvent({
            event: event,
            record: record,
            oldRecord: oldRecord,
            webhook: config.webhooks[i]
        });
    }
} else {
    var entityName = config.webhooksEntity;
    var urlField = config.webhooksUrlField;
    var tokenField = config.webhooksVerificationTokenField;

    var records = sys.data.find(entityName, {});
    //configured by entity records
    while (records.hasNext()) {
        var data = records.next();
        endpoint.publishEvent({
            event: event,
            record: record,
            oldRecord: oldRecord,
            webhook: {
                url: data.field(urlField).val().toString(),
                verificationToken: data.field(tokenField).val().toString()
            }
        });
    }
}
```

## About SLINGR

SLINGR is a low-code rapid application development platform that accelerates development, with robust architecture for integrations and executing custom workflows and automation.

[More info about SLINGR](https://slingr.io)

## License

This endpoint is licensed under the Apache License 2.0. See the `LICENSE` file for more details.


