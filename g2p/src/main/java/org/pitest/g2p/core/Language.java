package org.pitest.g2p.core;

public enum Language {
    ca_ES("Catalan"),
    cy_GB("Welsh"),
    da_DK("Danish"),
    de_DE("German"),
    en_US("English (North America)"),
    en_GB("English (United Kingdom)"),
    es_ES("Spanish"),
    et_EE("Estonian"),
    eu_ES("Basque"),
    fa_IR("Farsi/Persian"),
    fr_FR("French"),
    ga_IE("Irish"),
    hr_HR("Croatian"),
    hu_HU("Hungarian"),
    id_ID("Indonesian"),
    is_IS("Icelandic"),
    it_IT("Italian"),
    ja_JP("Japanese"),
    ko_KR("Korean"),
    nb_NO("Norwegian"),
    nl_NL("Dutch"),
    pl_PL("Polish"),
    pt_BR("Portuguese (Brazil)"),
    pt_PT("Portuguese"),
    qu_PE("Quechua"),
    ro_RO("Romanian"),
    sr_RS("Serbian"),
    sv_SE("Swedish"),
    tr_TR("Turkish"),
    yue_CN("Cantonese"),
    zh_CN("Chinese");

    private final String desc;

    Language(String desc) {
        this.desc = desc;
    }

    public String tag() {
        return name().replace("_", "-");
    }

    @Override
    public String toString() {
        return tag() + " (" + desc + ")";
    }
}
