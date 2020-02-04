package com.tadahtech.mc.staffmanage.util.sequences;

public class FloatSequence implements Sequence<Float> {
    private float current;
    private float interval;
    private float stop;
    private float start;
    private int iteration;
    private Runnable onRotation;

    public FloatSequence(float start, float stop, float interval) {
        this(start, stop, interval, null);
    }

    public FloatSequence(float start, float stop, float interval, Runnable onRotation) {
        this.start = start;
        this.stop = stop;
        this.interval = interval;
        this.current = start - interval;
        this.onRotation = onRotation;
    }

    @Override
    public Float get() {
        if (this.current < 0) {
            return 0f;
        }

        return this.current;
    }

    @Override
    public Float next() {
        float current = this.current;
        this.current += this.interval;
        if (this.current > this.stop) {
            this.current = 0f;
        }

        if ((current < this.start && this.start <= this.current) || (this.current == 0f && this.start == 0f)) {
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
    public Float getInterval() {
        return interval;
    }

    @Override
    public Float getStop() {
        return stop;
    }

    @Override
    public Float getStart() {
        return start;
    }

    @Override
    public void add(Float object) {
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
