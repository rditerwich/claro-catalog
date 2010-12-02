package claro.catalog.manager.client.widgets;

import static claro.catalog.manager.client.Util.i18n;

import java.util.HashMap;


import claro.catalog.manager.client.Util;
import claro.catalog.manager.client.Styles;

import com.google.common.base.Preconditions;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

public class UploadWidget extends Composite {

	private final Anchor uploadLink;
	private final PopupPanel popup = new PopupPanel(true, true);
	private final FormPanel formPanel = new FormPanel();
	
	// Parameters for upload
	private InputHidden itemId;
	private InputHidden pvId;
	private InputHidden propertyId;
	private InputHidden language;


	public UploadWidget() {
		initWidget(uploadLink = new Anchor(i18n.upload()){{
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
				setName(i18n.upload()); // Without this uploads don't work.
			}});
			add(new SimplePanel() {{
				add(new SubmitButton(i18n.uploadFile()));
			}});
			add(itemId = new InputHidden("itemId"));
			add(pvId = new InputHidden("propertyValueId"));
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
			this.pvId.setText(propertyValueId.toString());
		} else {
			this.pvId.setText(null);  // TODO is this allowed, or ""?
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
