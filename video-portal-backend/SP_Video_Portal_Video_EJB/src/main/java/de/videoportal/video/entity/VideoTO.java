/* (C)2023 */
package de.videoportal.video.entity;

import de.videoportal.video.entity.impl.Video;
import java.util.Collection;

public class VideoTO {

    private static final long serialVersionUID = 8748026369117387931L;

    long videoId;
    String titel;
    String beschreibung;
    String dateipfad;
    String metaData;
    int anzahlAufrufe;

    // Referenz zur Kategorie

    Collection<Long> unterKategorien;
    String name;
    ThemaTO thema;

    public VideoTO() {}

    public VideoTO(
            long videoId,
            String titel,
            String beschreibung,
            String dateipfad,
            String metaData,
            int anzahlAufrufe,
            Collection<Long> unterKategorien,
            String name,
            ThemaTO thema) {
        super();
        this.videoId = videoId;
        this.titel = titel;
        this.beschreibung = beschreibung;
        this.dateipfad = dateipfad;
        this.metaData = metaData;
        this.anzahlAufrufe = anzahlAufrufe;
        this.unterKategorien = unterKategorien;
        this.name = name;
        this.thema = thema;
    }

    public VideoTO(
            long videoId,
            String titel,
            String beschreibung,
            String dateipfad,
            String metaData,
            int anzahlAufrufe,
            Collection<Long> unterKategorien,
            String name,
            long themaid,
            String themaname) {
        super();
        this.videoId = videoId;
        this.titel = titel;
        this.beschreibung = beschreibung;
        this.dateipfad = dateipfad;
        this.metaData = metaData;
        this.anzahlAufrufe = anzahlAufrufe;
        this.unterKategorien = unterKategorien;
        this.name = name;
        this.thema = new ThemaTO(themaid, themaname);
    }

    public Video toVideo() {
        Video video =
                new Video(
                        this.getVideoId(),
                        this.getTitel(),
                        this.getBeschreibung(),
                        this.getDateipfad(),
                        this.getMetaData(),
                        this.getAnzahlAufrufe(),
                        this.getUnterKategorien(),
                        this.getName(),
                        this.getThema().toThema());
        return video;
    }

    public long getVideoId() {
        return videoId;
    }

    public void setVideoId(long videoId) {
        this.videoId = videoId;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getDateipfad() {
        return dateipfad;
    }

    public void setDateipfad(String dateipfad) {
        this.dateipfad = dateipfad;
    }

    public int getAnzahlAufrufe() {
        return anzahlAufrufe;
    }

    public void setAnzahlAufrufe(int anzahlAufrufe) {
        this.anzahlAufrufe = anzahlAufrufe;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public Collection<Long> getUnterKategorien() {
        return unterKategorien;
    }

    public void setUnterKategorien(Collection<Long> unterKategorien) {
        this.unterKategorien = unterKategorien;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ThemaTO getThema() {
        return thema;
    }

    public void setThema(ThemaTO thema) {
        this.thema = thema;
    }
}
