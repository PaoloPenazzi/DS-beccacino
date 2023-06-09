package it.unibo.sd.beccacino.model;

import it.unibo.sd.beccacino.Card;
import it.unibo.sd.beccacino.Value;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class BeccacinoCardComparator implements Comparator<Card> {
    private final Map<Value, Integer> valueMap;

    /**
     * Class constructor.
     */
    public BeccacinoCardComparator() {
        this.valueMap = new HashMap<>();
        this.createValueMap();
    }

    /**
     * {@inheritDoc}
     *
     * @param cardOne is the first card to consider
     * @param cardTwo is the second one
     */
    public int compare(final Card cardOne, final Card cardTwo) {
        final int valueCardOne = this.valueMap.get(cardOne.getValue());
        final int valueCardTwo = this.valueMap.get(cardTwo.getValue());
        return valueCardOne - valueCardTwo;
    }

    /**
     * It creates the value map, to each value it associates an integer.
     */
    private void createValueMap() {
        int count = 0;
        valueMap.put(Value.QUATTRO, count++);
        valueMap.put(Value.CINQUE, count++);
        valueMap.put(Value.SEI, count++);
        valueMap.put(Value.SETTE, count++);
        valueMap.put(Value.FANTE, count++);
        valueMap.put(Value.CAVALLO, count++);
        valueMap.put(Value.RE, count++);
        valueMap.put(Value.ASSO, count++);
        valueMap.put(Value.DUE, count++);
        valueMap.put(Value.TRE, count++);
    }
}
