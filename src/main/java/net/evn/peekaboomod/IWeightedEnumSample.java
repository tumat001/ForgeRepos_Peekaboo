package net.evn.peekaboomod;

import net.minecraftforge.event.level.BlockEvent;

public interface IWeightedEnumSample {

    public enum SampleType {
        LINK,
        NODE,
    }

    public int getWeight();

    public SampleType getSampleType();


    public int getLinkToEnumDetId();

    public void executeSampleTypeAsNode(BlockEvent.BreakEvent event);

}
