package org.ecom.common.model.event;


import io.micrometer.tracing.TraceContext;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TracingEventData
{
    private String traceId;
    private String spanId;
    private String parentId;

    public static TracingEventData from(TraceContext traceContext)
    {
        TracingEventData tracingEventData = new TracingEventData();
        tracingEventData.traceId = traceContext.traceId();
        tracingEventData.spanId = traceContext.spanId();
        tracingEventData.parentId = traceContext.parentId();
        return tracingEventData;
    }
}