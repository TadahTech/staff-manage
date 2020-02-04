package com.tadahtech.mc.staffmanage.util.sequences;

public class DoubleSequence implements Sequence<Double> {
    private double current;
    private double interval;
    private double stop;
    private double start;
    private int iteration;
    private Runnable onRotation;

    public DoubleSequence(double start, double stop, double interval) {
        this(start, stop, interval, null);
    }

    public DoubleSequence(double start, double stop, double interval, Runnable onRotation) {
        this.start = start;
        this.stop = stop;
        this.interval = interval;
        this.current = start - interval;
        this.onRotation = onRotation;
    }

    @Override
    public Double get() {
        if (this.current < 0) {
            return 0.0;
        }

        return this.current;
    }

    @Override
    public Double next() {
        double current = this.current;
        this.current += this.interval;
        if (this.current > this.stop) {
            this.current = 0;
        }

        if ((current < this.start && this.start <= this.current) || (this.current == 0 && this.start == 0)) {
            if (this.iteration > 0 && this.onRotation != null) {
                this.onRotation.run();
            }

            this.iteration++;
        }

        return this.current;
    }

    public int getIteration() {
        return iteration;
    }

    @Override
    public Double getInterval() {
        return interval;
    }

    @Override
    public Double getStop() {
        return stop;
    }

    @Override
    public Double getStart() {
        return start;
    }

    @Override
    public void add(Double object) {
        this.current += object;
    }

    @Override
    public void reset() {
        this.current = this.start - this.interval;
        this.iteration = 0;
    }

    @Override
    public void onIteration(Runnable runnable) {
        this.onRotation = runnable;
    }
}
