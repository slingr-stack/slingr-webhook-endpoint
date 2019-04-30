/**
 * Example of dynamic data listener created by endpoint configuration
 *
 * Created by egonzalez on 11/08/17.
 */

listeners.genericWebhookDispatcher = {
    label: 'Generic WebHook Dispatcher',
    type: 'dynamicData',
    callback: function(event, record, oldRecord) {
        var endpoint = app.endpoints[event.additionalInfo.endpoint];
        var config = endpoint._configuration;
        if (config.webhooksType == 'fixed') {
            for (var i in config.webhooks) {
                sys.logs.debug('Sending notification to: '+config.webhooks[i].url);
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
            while (records.hasNext()) {
                var data = records.next();
                sys.logs.debug('Sending notification to: '+data.field(urlField).val().toString());
                endpoint.publishEvent({
                    event: event,
                    record: (record ? record.toJson() : null),
                    oldRecord: (oldRecord ? oldRecord.toJson() : null),
                    webhook: {
                        url: data.field(urlField).val().toString(),
                        verificationToken: data.field(tokenField).val().toString()
                    }
                });
            }
        }
    }
};
