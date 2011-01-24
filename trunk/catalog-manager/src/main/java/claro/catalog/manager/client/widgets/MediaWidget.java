package claro.catalog.manager.client.widgets;

import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.i18n.I18NCatalog;

import com.google.common.base.Preconditions;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Image;

/**
 * Widget to display an PropertyType.MediaType value. If the mimeType is an
 * image an img tag is show else an url with the name (stringValue). When set
 * the widget will display the UploadWidget which enables the user to upload
 * files to the server.
 */
public class MediaWidget extends Composite implements HasClickHandlers, Globals {

	private Image image;
	private Anchor a;
	private UploadWidget up;
	private boolean uploadDataSet;
	private final boolean alwaysShow;

	public MediaWidget() {
		this(false, false);
	}

	/**
	 * Creates MediaWidget if canUpload is false no upload button will be
	 * displayed, meaning the value can't be changed. Use when only display
	 * status needed.
	 * 
	 * @param canUpload
	 */
	public MediaWidget(final boolean canUpload, boolean alwaysShow) {
		this.alwaysShow = alwaysShow;
		initWidget(new FlowPanel() {{
			add(image = new Image() {{
				setVisible(false);
				setSize("70px", "70px");
			}});
			add(a = new Anchor() {{
				setTarget("_blank");
				setVisible(false);
			}});
			if (canUpload) {
				add(up = new UploadWidget() {{
					addSubmitCompleteHandler(new SubmitCompleteHandler() {
						@Override
						public void onSubmitComplete(SubmitCompleteEvent event) {
							up.hide();
							StatusMessage.show(messages.fileUploaded(event.getResults()));
						}
					});
				}});
				
			}
		}});
	}
	
	
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return image.addClickHandler(handler);
	}
	
	public HandlerRegistration addSubmitCompleteHandler(SubmitCompleteHandler handler) {
	    return up.addSubmitCompleteHandler(handler);
	}

	public void setData(Long propertyValueId, String mimeType, String filename) {
		Preconditions.checkState(up == null || uploadDataSet, "Widget can upload.  setUploadData() must be called before setData()");
		render(propertyValueId, mimeType, filename);
	}
	
	public void setUploadData(Long itemId, Long propertyId, Long propertyValueId, String language) {
		Preconditions.checkState(up != null, "setUploadData() called on a mediawidget that does not support uploads");
		
		up.setParams(itemId, propertyId, propertyValueId, language);
		uploadDataSet = true;
	}

	private void render(Long id, String mimeType, String filename) {
		if (id == null || mimeType == null || "".equals(mimeType.trim()) ) {
			image.setVisible(alwaysShow);
			a.setVisible(false);
			image.setUrl("");
			image.setTitle("");
			a.setHref("");
			a.setText("");
		} else {
			mimeType = mimeType.trim();
			if (filename != null) {
				filename = filename.trim();
			}
			if (mimeType.startsWith("image")) {
				image.setVisible(true);
				a.setVisible(false);
				image.setUrl(GWT.getModuleBaseURL() + "DownloadMedia?pvId=" + id);
				image.setTitle(filename);
				a.setHref("");
				a.setText("");
			} else {
				a.setVisible(true);
				image.setVisible(false);
				image.setUrl("");
				image.setTitle("");
				a.setHref(GWT.getModuleBaseURL() + "DownloadMedia?pvId=" + id);
				a.setText(filename);
			}
		}
	}

	public void setImageSize(String width, String height) {
		image.setSize(width, height);
	}
}
