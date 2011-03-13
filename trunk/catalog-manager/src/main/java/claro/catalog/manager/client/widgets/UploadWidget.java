package claro.catalog.manager.client.widgets;

import claro.catalog.manager.client.Globals;

import com.google.common.base.Preconditions;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SubmitButton;

public class UploadWidget extends Composite implements Globals {

	private final Anchor uploadLink;
	private final PopupPanel popup = new PopupPanel(true, true);
	private final FormPanel formPanel = new FormPanel();
	
	// Parameters for upload
	private InputHidden itemId;
	private InputHidden mediaContentId;
	private InputHidden propertyId;
	private InputHidden language;


	public UploadWidget() {
		initWidget(uploadLink = new Anchor(messages.upload()){{
			addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					popup.showRelativeTo(uploadLink);
				}
			});
		}});
		popup.add(formPanel);
		formPanel.add(new FlowPanel() {{
			add(new HTML("Browse a file and press upload file to confirm."));
			add(new FileUpload() {{
				setName(messages.upload()); // Without this uploads don't work.
			}});
			add(new SimplePanel() {{
				add(new SubmitButton(messages.uploadFile()));
			}});
			add(itemId = new InputHidden("itemId"));
			add(mediaContentId = new InputHidden("propertyValueId"));
			add(propertyId = new InputHidden("propertyId"));
			add(language = new InputHidden("language"));
		}});
		formPanel.setAction(GWT.getModuleBaseURL() + "UploadMedia");
		formPanel.setMethod(FormPanel.METHOD_POST);
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
	}
	
	public void setParams(Long itemId, Long propertyId, Long propertyValueId, String language) {
		Preconditions.checkNotNull(itemId);
		Preconditions.checkNotNull(propertyId);
		
		// These two are compulsory.
		this.itemId.setText(itemId.toString());
		this.propertyId.setText(propertyId.toString());
		
		if (propertyValueId != null) {
			this.mediaContentId.setText(propertyValueId.toString());
		} else {
			this.mediaContentId.setText(null);  // TODO is this allowed, or ""?
		}
		
		if (language != null) {
			this.language.setText(language);
		} else {
			this.language.setText(null);
		}
	}
	

	public HandlerRegistration addSubmitCompleteHandler(SubmitCompleteHandler handler) {
		return formPanel.addSubmitCompleteHandler(handler);
	}

	public void hide() {
		popup.hide();
	}
}
