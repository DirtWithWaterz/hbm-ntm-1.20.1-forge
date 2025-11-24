package com.hbm.nucleartech.item.client;

import com.hbm.nucleartech.item.custom.ArmorModificationTableItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ArmorModificationTableItemRenderer extends GeoItemRenderer<ArmorModificationTableItem> {

    public ArmorModificationTableItemRenderer() {
        super(new ArmorModificationTableItemModel());
    }
}
