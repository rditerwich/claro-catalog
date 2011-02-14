package claro.catalog.manager.client.webshop;

import java.util.Date;

import claro.catalog.command.shop.StorePromotion;
import claro.catalog.manager.client.taxonomy.CategoryProperties.Styles;
import claro.jpa.shop.Promotion;
import claro.jpa.shop.VolumeDiscountPromotion;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;

import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.gwt.client.widgets.MoneyWidget;
import easyenterprise.lib.gwt.client.widgets.TableWithObjects;
import easyenterprise.lib.util.Money;

public class WebshopPromotionsPanel extends TableWithObjects<Promotion> {
	private static final int PRODUCT_COL = 0;
	private static final int FROMDATE_COL = 1;
	private static final int TODATE_COL = 2;
	private static final int PROMOTIONTYPE_COL = 3;
	private static final int PRICE_COL = 4;
	private static final int VOLUME_COL = 5;
	private static final int REMOVE_COL = 6;
	private static final int NR_COL = 7;
	

	private final ShopModel model;

	public WebshopPromotionsPanel(ShopModel model_) {
		this.model = model_;
		resizeColumns(NR_COL);
		setHeaderText(0, PRODUCT_COL, "product");
		setHeaderText(0, FROMDATE_COL, "valid from");
		setHeaderText(0, TODATE_COL, "valid to");
		setHeaderText(0, PROMOTIONTYPE_COL, "kind");
		setHeaderText(0, PRICE_COL, "price");
		setHeaderText(0, VOLUME_COL, "volume");
	}

	public void render() {
		int fromRows = getRowCount();
		resizeRows(model.getPromotions().size());
		
		// Create new Rows
		for (int row = fromRows; row < model.getPromotions().size(); row++) {
			setObject(row, model.getPromotions().get(row));
			
			final int r = row;
			setWidget(row, PRODUCT_COL, new Label() {{
			}});
			setWidget(row, FROMDATE_COL, new DateBox() {{
				setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM)));
				addValueChangeHandler(new ValueChangeHandler<Date>() {
					public void onValueChange(ValueChangeEvent<Date> event) {
						doPromotionChanged(r);
					}
				});
			}});
			setWidget(row, TODATE_COL, new DateBox() {{
				setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM)));
				addValueChangeHandler(new ValueChangeHandler<Date>() {
					public void onValueChange(ValueChangeEvent<Date> event) {
						doPromotionChanged(r);
					}
				});
			}});
			setWidget(row, PROMOTIONTYPE_COL, new Label("Volume Discount") {{ // TODO i18n
			}});
			setWidget(row, PRICE_COL, new MoneyWidget() {
				protected void valueChanged(Money newValue) {
					doPromotionChanged(r);
				}
			});
			setWidget(row, VOLUME_COL, new TextBox() {{
				addValueChangeHandler(new ValueChangeHandler<String>() {
					public void onValueChange(ValueChangeEvent<String> event) {
						doPromotionChanged(r);
					}
				});
			}});
			setWidget(row, REMOVE_COL, new Image() {{
				StyleUtil.addStyle(this, Styles.clear);
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							doRemovePromotion(r);
						}
					});
				}
			}); 

		}
		
		// render all rows
		for (int row = 0; row < model.getPromotions().size(); row++) {
			renderRow(row);
		}
		
	}
	
	
	private void doRemovePromotion(int r) {
		Promotion promotion = getObject(r);
		
		StorePromotion command = new StorePromotion(promotion);
		command.removePromotion = true;
		model.store(command);
	}
	protected void doPromotionChanged(int row) {
		Promotion promotion = getObject(row);

		promotion.setStartDate(((HasValue<Date>) getWidget(row, FROMDATE_COL)).getValue());
		promotion.setStartDate(((HasValue<Date>) getWidget(row, TODATE_COL)).getValue());
		if (promotion instanceof VolumeDiscountPromotion) {
			VolumeDiscountPromotion volumePromotion = (VolumeDiscountPromotion) promotion;
			volumePromotion.setPrice(((MoneyWidget) getWidget(row, PRICE_COL)).getValue().value);
			volumePromotion.setPriceCurrency(((MoneyWidget) getWidget(row, PRICE_COL)).getValue().currency);
			volumePromotion.setVolumeDiscount(Integer.parseInt(((HasText) getWidget(row, VOLUME_COL)).getText()));
		}
		
		StorePromotion command = new StorePromotion(promotion);
		model.store(command);
	}

	@SuppressWarnings("unchecked")
	public void renderRow(int row) {
		Promotion promotion = model.getPromotions().get(row);
		setObject(row, promotion);
		((HasValue<Date>) getWidget(row, FROMDATE_COL)).setValue(promotion.getStartDate());
		((HasValue<Date>) getWidget(row, TODATE_COL)).setValue(promotion.getEndDate());
		if (promotion instanceof VolumeDiscountPromotion) {
			VolumeDiscountPromotion volumePromotion = (VolumeDiscountPromotion) promotion;
			if (volumePromotion.getProduct() != null) {
				((Label) getWidget(row, PRODUCT_COL)).setText(model.getReferredProducts().getOrEmpty(volumePromotion.getProduct().getId()).tryGet(model.getLanguage(), null));
			}
			((MoneyWidget) getWidget(row, PRICE_COL)).setValue(new Money(volumePromotion.getPrice(), volumePromotion.getPriceCurrency()));
			((HasText) getWidget(row, VOLUME_COL)).setText(volumePromotion.getVolumeDiscount().toString());
		}
	}

}
