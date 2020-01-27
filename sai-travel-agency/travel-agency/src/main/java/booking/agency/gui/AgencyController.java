package booking.agency.gui;

import booking.agency.gateway.AgencyAppGateway;
import booking.agency.model.AgencyReply;
import booking.agency.model.AgencyRequest;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

public class AgencyController implements Initializable {

     public String agencyName;
     public String queueName;

    private final Logger logger = LoggerFactory.getLogger(getClass());

     @FXML
     public TextField tfPrice;

    @FXML
    public ListView<AgencyListViewLine> lvAgencyRequestReply;

    private AgencyAppGateway agencyAppGateway;

    //initiates AgencyController with queueName & agencyName
    public AgencyController(String queueName, String agencyName){
        this.agencyName = agencyName;
        this.queueName=queueName;
        this.agencyAppGateway = new AgencyAppGateway(queueName) {
            @Override
            public void onRequestReceived(AgencyRequest agencyRequest) {
                AgencyListViewLine listViewLine = new AgencyListViewLine(agencyRequest);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        lvAgencyRequestReply.getItems().add(listViewLine);

                    }
                });

            }
        };
    }

    @FXML
    public void btnSendAgencyReplyClicked(){
        AgencyListViewLine listViewLine = lvAgencyRequestReply.getSelectionModel().getSelectedItem();
        if (listViewLine!= null){
            if (listViewLine.getReply() == null) {
                double price = Double.parseDouble(tfPrice.getText());
                String id = listViewLine.getRequest().getId();
                AgencyReply reply = new AgencyReply(id, agencyName, price);

                listViewLine.setReply(reply);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        lvAgencyRequestReply.refresh();
                    }
                });
                // TODO: send the agency reply
                agencyAppGateway.send(reply);
                logger.info("Send here the reply: " + reply);
            } else {
                showErrorMessageDialog("You have already sent reply for this request.");
            }
        } else {
            showErrorMessageDialog("No request is selected.\nPlease select a request for which you want to send the reply.");
        }

    }

    private void showErrorMessageDialog(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Travel Agency");
        alert.setHeaderText("Error occurred while sending price offer.");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
