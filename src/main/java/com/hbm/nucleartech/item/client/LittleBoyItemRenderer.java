package com.hbm.nucleartech.item.client;

import com.hbm.nucleartech.item.custom.LittleBoyItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class LittleBoyItemRenderer extends GeoItemRenderer<LittleBoyItem> {

    public LittleBoyItemRenderer() {
        super(new LittleBoyItemModel());
    }
}
