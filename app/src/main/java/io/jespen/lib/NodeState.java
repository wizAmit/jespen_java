package io.jespen.lib;

public enum NodeState {
    Uninitialized {
        @Override
        public NodeState nextState() {
            return Initialized;
        }
    },
    Initialized {
        @Override
        public NodeState nextState() {
            return this;
        }
    };

    public abstract NodeState nextState();
}
