package com.hbm.nucleartech.item.client;

import com.hbm.nucleartech.item.custom.GraphiteBlockItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GraphiteBlockItemRenderer extends GeoItemRenderer<GraphiteBlockItem> {

    public GraphiteBlockItemRenderer() {
        super(new GraphiteBlockItemModel());
    }
}
