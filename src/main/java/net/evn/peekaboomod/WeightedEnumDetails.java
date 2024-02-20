package net.evn.peekaboomod;

import net.minecraftforge.event.level.BlockEvent;

import java.util.Random;

public class WeightedEnumDetails {

    private final int total_weight;
    private IWeightedEnumSample[] samples;
    private Random rng;
    WeightedEnumDetails(int arg_final_weight) {
        total_weight = arg_final_weight;
    }
    WeightedEnumDetails(IWeightedEnumSample[] arg_samples, Random arg_rng) {
        int total = 0;
        for (IWeightedEnumSample sample: arg_samples) {
            total += sample.getWeight();
        }
        total_weight = total;
        samples = arg_samples;
        rng = arg_rng;
    }

    public int getFinalWeight() {
        return total_weight;
    }


    public IWeightedEnumSample getRandomEnumSample() {
        int randi = rng.nextInt(0, total_weight);
        int total = 0;
        for (IWeightedEnumSample sample : samples) {
            total += sample.getWeight();
            if (total > randi) {
                return sample;
            }
        }

        return samples[samples.length - 1]; //should not reach here
    }

    public IWeightedEnumSample getRandomEnumSample_NestUpToNonEnumLinkAndExecute(BlockEvent.BreakEvent event, StoreOfGameProbabilities store_of_game_prob) {
        IWeightedEnumSample sample = getRandomEnumSample();
        while (sample.getSampleType() != IWeightedEnumSample.SampleType.NODE) {
            WeightedEnumDetails det = store_of_game_prob.getEnumDetailsFromId(sample.getLinkToEnumDetId());
            sample = det.getRandomEnumSample();
        }
        //now, sample is a node type.

        sample.executeSampleTypeAsNode(event);

        return sample;
    }

}
