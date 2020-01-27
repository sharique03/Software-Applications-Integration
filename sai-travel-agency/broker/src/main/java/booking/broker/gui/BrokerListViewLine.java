package booking.broker.gui;


import booking.agency.model.AgencyReply;
import booking.agency.model.AgencyRequest;
import booking.client.model.ClientBookingReply;
import booking.client.model.ClientBookingRequest;

import java.time.format.DateTimeFormatter;

/**
 * This class is an item/line for a ListViewLine. It makes it possible to put both BankInterestRequest and BankInterestReply object in one item in a ListViewLine.
 */
class BrokerListViewLine {
	
	private AgencyRequest agencyRequest;
	private AgencyReply agencyReply;
	private ClientBookingRequest clientBookingRequest;
	private ClientBookingReply clientBookingReply;
	
	public BrokerListViewLine(AgencyRequest agencyRequest) {
		setAgencyRequest(agencyRequest);
		setAgencyReply(null);
	}

	public BrokerListViewLine(ClientBookingRequest clientBookingRequest) {
		setClientBookingRequest(clientBookingRequest);
		setClientBookingReply(null);
	}

	private void setClientBookingRequest(ClientBookingRequest clientBookingRequest) {
		this.clientBookingRequest = clientBookingRequest;
	}


	public void setClientBookingReply(ClientBookingReply clientBookingReply) {
		this.clientBookingReply = clientBookingReply;
	}
	public ClientBookingRequest getClientBookingRequest() {
		return clientBookingRequest;
	}

	public AgencyRequest getAgencyRequest() {
		return agencyRequest;
	}
	
	private void setAgencyRequest(AgencyRequest agencyRequest) {
		this.agencyRequest = agencyRequest;
	}

	public void setAgencyReply(AgencyReply agencyReply) {
		this.agencyReply = agencyReply;
	}

    /**
     * This method defines how one line is shown in the ListViewLine.
     * @return
     *  a) if BankInterestReply is null, then this item will be shown as "bankInterestRequest.toString ---> waiting for loan reply..."
     *  b) if BankInterestReply is not null, then this item will be shown as "bankInterestRequest.toString ---> bankInterestReply.toString"
     */
	@Override
	public String toString() {

		DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd/MM/uuuu");
		final String delimiter = "-";
		final String requestString = clientBookingRequest.getOriginAirport() +delimiter
				+ clientBookingRequest.getDestinationAirport() + delimiter
				+ clientBookingRequest.getNumberOfTravellers() + delimiter
				+ clientBookingRequest.getDate().format(formatters) + delimiter
				+ clientBookingRequest.getClientID();
		final String replyString;
		if (agencyReply == null){
			replyString = "waiting...";
		} else {
			replyString =  agencyReply.getPrice() + delimiter + agencyReply.getName();
		}
		return requestString + "  --->  " + replyString;

	  // return clientBookingRequest.toString() + "  --->  " + ((agencyReply !=null)? agencyReply.toString():"waiting for loan reply...");
	}
	
}
