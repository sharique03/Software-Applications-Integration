package booking.broker.gui;

import booking.agency.model.AgencyReply;
import booking.agency.model.AgencyRequest;
import booking.broker.administration.Administration;
import booking.broker.gateway.AdministrationAppGateway;
import booking.client.model.ClientBookingReply;
import booking.client.model.ClientBookingRequest;
import booking.broker.gateway.BrokerAppGateway_Agency;
import booking.broker.gateway.BrokerAppGateway_Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import org.slf4j.LoggerFactory;
import java.net.URL;

import java.util.ArrayList;
import java.util.ResourceBundle;

class BrokerController implements Initializable {

    //list of administration response to map with the response from agency and apply discount
    private ArrayList<Administration> customerProfileMap=new ArrayList<>();

     @FXML
    public ListView<BrokerListViewLine> lvBrokerRequestReply;

    private BrokerAppGateway_Agency brokerAppGatewayAgency = new BrokerAppGateway_Agency() {
        @Override
        public void onReplyReceived(AgencyRequest agencyRequest,AgencyReply agencyReply) {

            ClientBookingRequest clientBookingRequest=getRequestReply(agencyReply);
            double totalPrice=determineTotalPrice(clientBookingRequest,agencyReply);
            ClientBookingReply clientBookingReply = new ClientBookingReply(agencyReply.getId(), agencyReply.getName(),totalPrice);
            clientAppGateway.send(clientBookingReply);

        }
    };

    /**
     * calculate total price on the basis of clientBookingRequest & agencyReply
     * @param clientBookingRequest
     * @param agencyReply
     * @return
     */
    private double determineTotalPrice(ClientBookingRequest clientBookingRequest, AgencyReply agencyReply) {

        double totalPrice;
        double discount=0.0;
        int nrOfPassengers=clientBookingRequest.getNumberOfTravellers();
        double agencyPrice=agencyReply.getPrice();

        if(clientBookingRequest.getClientID()!=0)
        {
            for (Administration a:customerProfileMap) {
                if(a.getId().equals(agencyReply.getId()))
                {
                    discount=a.getDiscount();
                    System.out.println("administration  service response"+ a.toString());
                   break;
                }
            }
            totalPrice=(nrOfPassengers*agencyPrice)*(1-discount/100);
        }
        //no discount for clientRequest without any clientId->admin call was not needed
        else
        {
            totalPrice=(nrOfPassengers*agencyPrice);
        }


        return totalPrice;
    }

    //receives loanRequest and sends bankInterestRequest
    private BrokerAppGateway_Client clientAppGateway = new BrokerAppGateway_Client() {
        @Override
        public void onRequestReceived(ClientBookingRequest clientBookingRequest) {
            BrokerListViewLine listViewLine = new BrokerListViewLine(clientBookingRequest);

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    lvBrokerRequestReply.getItems().add(listViewLine);

                }
            });
            //client id will never be null it can be 0 if not filled
            Boolean isRegisteredClient=determineClientRegistration(clientBookingRequest);
            AgencyRequest agencyRequest=new AgencyRequest(clientBookingRequest.getId(),clientBookingRequest.getOriginAirport(),clientBookingRequest.getDestinationAirport(),clientBookingRequest.getDate(),clientBookingRequest.getNumberOfTravellers(),isRegisteredClient);
            brokerAppGatewayAgency.sendRequestToAgencies(agencyRequest);
         }
    };

    public BrokerController() {
        LoggerFactory.getLogger(getClass()).info("Created BrokerController");

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * This method returns the line of lvMessages which contains the given loan request.
     * Refreshes the screen with the reply received from agency
     * @return The ListViewLine line of lvMessages which contains the given request
     */

    private ClientBookingRequest getRequestReply(AgencyReply reply) {
        ClientBookingRequest clientBookingRequest=new ClientBookingRequest();
        for (int i = 0; i < lvBrokerRequestReply.getItems().size(); i++) {
            BrokerListViewLine rr = lvBrokerRequestReply.getItems().get(i);
            if (rr.getClientBookingRequest() != null && rr.getClientBookingRequest().getId().equals(reply.getId())) {
                rr.setAgencyReply(reply);
                clientBookingRequest=rr.getClientBookingRequest();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        lvBrokerRequestReply.refresh();
                    }
                });

                break;
            }
        }

        return clientBookingRequest;
    }

    /**
     * calls admin service to get discount & type
     * based on res received & clientId, determines if registered client or not
     * @param request
     * @return
     */
    private Boolean determineClientRegistration(ClientBookingRequest request)
    {
        Boolean isRegisteredClient=false;
        if(request.getClientID() !=0)
        {
            Administration customerProfile= AdministrationAppGateway.getCustomerProfile(request.getClientID(),request.getId());
            if(customerProfile!=null)
            {
                customerProfileMap.add(customerProfile);
                isRegisteredClient=true;
            }

        }
        return isRegisteredClient;
    }


}
