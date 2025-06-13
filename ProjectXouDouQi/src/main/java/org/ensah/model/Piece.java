package org.ensah.model;

public enum Piece {
    ELEPHANT(8),
    LION(7),
    TIGER(6),
    PANTHER(5),
    DOG(4),
    WOLF(3),
    CAT(2),
    RAT(1);

    private final int rank;

    Piece(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    public boolean canCapture(Piece other) {
        if (this == RAT && other == ELEPHANT) return true;
        if (this == ELEPHANT && other == RAT) return false;
        return this.rank >= other.rank;
    }
}