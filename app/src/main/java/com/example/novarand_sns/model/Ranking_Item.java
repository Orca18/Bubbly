package com.example.novarand_sns.model;

public class   Ranking_Item {

    // 순위(i), 순위권 키워드, 키워드 언급 수
    private int rank;
    private String keyword;
    private int keyword_count;

    public Ranking_Item(int rank, String keyword, int keyword_count) {
        this.rank = rank;
        this.keyword = keyword;
        this.keyword_count = keyword_count;
    }

    public int getRank() {
        return rank;
    }

    public String getKeyword() {
        return keyword;
    }

    public int getKeyword_count() {
        return keyword_count;
    }
}
