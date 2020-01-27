package booking.broker.gateway;

import booking.agency.model.AgencyRequest;

public enum AgencyQueues {
    BUSINESS_TICKETS("businessQueue"),
    CHEAP_TICKETS("cheapQueue"),
    EASY_TICKETS("easyQueue");

    private String queue;

    AgencyQueues(String queue) {
        this.queue=queue;
    }

    public String getQueue() {
        return queue;
    }

    //
    public boolean evaluate(AgencyRequest agencyRequest)
    {
        if(agencyRequest.isRegisteredClient() && this.equals(AgencyQueues.BUSINESS_TICKETS)){
            return true;
        }
        if (agencyRequest.getNrTravellers() > 2 && this.equals(AgencyQueues.CHEAP_TICKETS)) {
                return true;
            }
       if(this.equals(AgencyQueues.EASY_TICKETS))
            {
                return true;
            }
      return false;
    }
}
