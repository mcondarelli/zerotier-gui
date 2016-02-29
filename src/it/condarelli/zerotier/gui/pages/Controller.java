package it.condarelli.zerotier.gui.pages;

import java.util.List;

import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.decoration.StyleClassValidationDecoration;
import org.tbee.javafx.scene.layout.MigPane;

import com.github.edouardswiac.zerotier.ZTService;
import com.github.edouardswiac.zerotier.api.ZTCNetwork;
import com.github.edouardswiac.zerotier.api.ZTController;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Modality;

public class Controller extends FxController {
	private final class ChangeHandler implements ChangeListener<Object> {
		@Override
		public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
			setButtons();
		}
	}

	@FXML private MigPane						mpDispatcher;
	@FXML private Text							txtInstanceId;
	@FXML private ImageView					ivOK;
	@FXML private ComboBox<String>	cbNetworks;
	@FXML private MigPane						mpNetwork;
	@FXML private TextField					tfName;
	@FXML private ToggleButton			tbPrivate;
	@FXML private ImageView					ivPrivate;
	@FXML private ComboBox<String>	cbAssignModeV4;
	@FXML private ToggleButton			tbBroadcast;
	@FXML private ImageView					ivBroadcast;
	@FXML private ComboBox<String>	cbAssignModeV6;
	@FXML private ToggleButton			tbBridging;
	@FXML private ImageView					ivBridging;
	@FXML private Button						btnDel;
	@FXML private Button						btnNew;
	@FXML private Button						btnSave;

	private ZTService								zts;
	private ZTCNetwork							ztcn;

	private ValidationSupport				validator			= new ValidationSupport();
	private ChangeHandler						changeHandler	= new ChangeHandler();

	// Controller() {
	// super();
	// validator.setValidationDecorator(new StyleClassValidationDecoration());
	// validator.initInitialDecoration();
	// validator.setErrorDecorationEnabled(true);
	//
	// }

	@Override
	protected void setNode(Node n) {
		super.setNode(n);
		zts = Login.getService();
		if (zts != null) {
			ZTController ztc = zts.controller();
			if (ztc != null) {
				txtInstanceId.setText(ztc.getInstanceId());
				mpNetwork.setVisible(false);
				onRefresh();
				mpDispatcher.setVisible(true);
			} else
				mpDispatcher.setVisible(false);
		}
		validator.registerValidator(tfName, true, Validator.createEmptyValidator("A name is required"));
		// Private defaults to true
		// Broadcast defaults to false
		// Bridging defaults to false
		validator.registerValidator(cbAssignModeV4, Validator.createEmptyValidator("Selection required"));
		validator.registerValidator(cbAssignModeV6, Validator.createEmptyValidator("Selection required"));
		/*
		 * TODO: protected int multicastLimit; protected List<ZTCRelay> relays;
		 * protected List<String> ipLocalRoutes; protected List<ZTCAssignmet>
		 * ipAssignmentPools; protected List<ZTCRule> rules;
		 */
		// change monitor
		tfName.textProperty().addListener(changeHandler);
		tbPrivate.selectedProperty().addListener(changeHandler);
		// Private defaults to true
		// Broadcast defaults to false
		// Bridging defaults to false
		cbAssignModeV4.selectionModelProperty().addListener(changeHandler);
		cbAssignModeV6.selectionModelProperty().addListener(changeHandler);
		/*
		 * TODO: protected int multicastLimit; protected List<ZTCRelay> relays;
		 * protected List<String> ipLocalRoutes; protected List<ZTCAssignmet>
		 * ipAssignmentPools; protected List<ZTCRule> rules;
		 */
	}

	@FXML
	void onRefresh() {
		String s = cbNetworks.getSelectionModel().getSelectedItem();
		List<String> nets = zts.getCNetworks();
		cbNetworks.setItems(FXCollections.observableArrayList(nets));
		if (nets.contains(s)) {
			cbNetworks.getSelectionModel().select(s);
			setButtons();
		} else {
			cbNetworks.getSelectionModel().select(null);
			mpNetwork.setVisible(false);
			setButtons(false);
		}
		ivOK.setImage(new Image(getClass().getResourceAsStream("icons/16x16/dialog-ok-apply-4.png")));
	}

	@FXML
	void onSelectNet() {
		// final ScheduledThreadPoolExecutor executor = new
		// ScheduledThreadPoolExecutor(1);
		// executor.schedule(() -> {
		String s = cbNetworks.getSelectionModel().getSelectedItem();
		if (s != null && !s.trim().isEmpty()) {
			ztcn = zts.getCNetwork(s);

			tfName.setText(ztcn.getName());
			setPrivate(ztcn.isPrivate());
			cbAssignModeV4.getSelectionModel().select(ztcn.getV4AssignMode());
			setBroadcast(ztcn.isEnableBroadcast());
			cbAssignModeV6.getSelectionModel().select(ztcn.getV6AssignMode());
			setBridging(ztcn.isAllowPassiveBridging());

			mpNetwork.setVisible(true);
		} else
			mpNetwork.setVisible(false);
		// }, 2, TimeUnit.SECONDS);
	}

	private void setPrivate(boolean priv) {
		tbPrivate.setSelected(priv);
		onTogglePrivate();
	}

	private void setBroadcast(boolean bcast) {
		tbBroadcast.setSelected(bcast);
		onToggleBroadcast();
	}

	private void setBridging(boolean bridge) {
		tbBridging.setSelected(bridge);
		onToggleBridging();
	}

	@FXML
	void onSelectAMV4() {

	}

	@FXML
	void onSelectAMV6() {

	}

	@FXML
	void onToggleBridging() {
		ivBridging.setImage(new Image(getClass()
				.getResourceAsStream(tbBridging.isSelected() ? "icons/16x16/link.png" : "icons/16x16/link-break.png")));
	}

	@FXML
	void onToggleBroadcast() {
		ivBroadcast.setImage(new Image(getClass()
				.getResourceAsStream(tbBroadcast.isSelected() ? "icons/16x16/irkickflash.png" : "icons/16x16/irkickoff.png")));
	}

	@FXML
	void onTogglePrivate() {
		ivPrivate.setImage(new Image(getClass().getResourceAsStream(
				tbPrivate.isSelected() ? "icons/16x16/object-locked.png" : "icons/16x16/object-unlocked.png")));
	}

	private boolean checkChanged() {
		if (ztcn == null)
			return true;
		if (!ztcn.getName().equals(tfName.getText()))
			return true;
		if (ztcn.isPrivate() != tbPrivate.isSelected())
			return true;
		if (ztcn.isEnableBroadcast() != tbBroadcast.isSelected())
			return true;
		if (ztcn.isAllowPassiveBridging() != tbBridging.isSelected())
			return true;
		if (!ztcn.getV4AssignMode().equals(cbAssignModeV4.getSelectionModel().getSelectedItem()))
			return true;
		if (!ztcn.getV6AssignMode().equals(cbAssignModeV6.getSelectionModel().getSelectedItem()))
			return true;
		/*
		 * TODO: protected int multicastLimit; protected List<ZTCRelay> relays;
		 * protected List<String> ipLocalRoutes; protected List<ZTCAssignmet>
		 * ipAssignmentPools; protected List<ZTCRule> rules;
		 */
		return false;
	}

	private void setButtons() {
		setButtons(checkChanged());
	}

	private void setButtons(boolean changed) {
		boolean invalid = (validator != null) ? validator.isInvalid() : true;
		btnDel.setDisable(ztcn == null);
		btnNew.setDisable(changed);
		btnSave.setDisable(!changed || invalid);
	}

	@FXML
	void onDel() {
		Alert dlg = createAlert(AlertType.CONFIRMATION);
		dlg.setTitle("Delete Network");
		dlg.getDialogPane().setContentText(String.format("Network '%s' (%s) \nwill be DESTROYED, are you SURE?", ztcn.getName(), ztcn.getNwid()));
		dlg.getDialogPane().setHeaderText("Destroy current Network?");
		if (ztcn == null) {
			(new Alert(AlertType.ERROR, "Current ZTCNetwork is null; nothing to delete!")).showAndWait();
		} else {
			dlg.showAndWait().ifPresent(result -> {
				if (result == ButtonType.OK) {
					String nwid = ztcn.getNwid();
					zts.deleteCNetwork(nwid);
					ztcn = null;
					cbNetworks.getSelectionModel().clearSelection();
					cbNetworks.getItems().remove(nwid);
				}
			});
		}
	}

	@FXML
	void onNew() {
		TextInputDialog dlg = new TextInputDialog("______");
		ValidationSupport vs = new ValidationSupport();
		vs.registerValidator(dlg.getEditor(), true, Validator
				.createRegexValidator("Id must be 6 hex chars or 6 underscores", "______|\\p{XDigit}{6}", Severity.ERROR));
		dlg.setTitle("New Network");
		dlg.getDialogPane().setContentText("Enter Network ID \n(or leave 6 underscores to \nhave it random generated)");
		dlg.getDialogPane().setHeaderText("Create new Network?");
		dlg.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(vs.invalidProperty());
		dlg.showAndWait().ifPresent(result -> {
			String ni = Login.getControllerAddress() + result;
			ZTCNetwork n = fillZTCNetwork(new ZTCNetwork(ni));
			n = zts.updateCNetwork(n);
			ni = n.getNwid();
			cbNetworks.getItems().add(ni);
			cbNetworks.getSelectionModel().select(ni);
		});
	}

	@FXML
	void onSave() {
		Alert dlg = createAlert(AlertType.CONFIRMATION);
		dlg.setTitle("Save");
		if (ztcn == null) {
			(new Alert(AlertType.ERROR, "Current ZTCNetwork is null; can't save!")).showAndWait();
		} else {
			dlg.getDialogPane().setContentText("Update existing ZeroTier Network?");
			dlg.getDialogPane().setHeaderText("ZeroTier Controller interaction");
			dlg.showAndWait().ifPresent(result -> {
				if (result == ButtonType.OK) {
					fillZTCNetwork(ztcn);
					zts.updateCNetwork(ztcn);
				}
			});
		}
	}

	private ZTCNetwork fillZTCNetwork(ZTCNetwork ztcn) {
		ztcn.setName(tfName.getText());
		ztcn.setPrivate(tbPrivate.isSelected());
		ztcn.setEnableBroadcast(tbBroadcast.isSelected());
		ztcn.setAllowPassiveBridging(tbBridging.isSelected());
		ztcn.setV4AssignMode(cbAssignModeV4.getSelectionModel().getSelectedItem());
		ztcn.setV6AssignMode(cbAssignModeV6.getSelectionModel().getSelectedItem());
		/*
		 * TODO: protected int multicastLimit; protected List<ZTCRelay> relays;
		 * protected List<String> ipLocalRoutes; protected List<ZTCAssignmet>
		 * ipAssignmentPools; protected List<ZTCRule> rules;
		 */
		return ztcn;
	}

	private Alert createAlert(AlertType type) {
		Alert dlg = new Alert(type);
		dlg.initModality(Modality.APPLICATION_MODAL);
		// dlg.initOwner(owner);
		return dlg;
	}

}
