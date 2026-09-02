package it.unicam.cs.mpgc.rpg129543.api;

/**
 * Data Transfer Object (DTO) che trasporta l'esito di una sfida
 * dal Model al Controller, senza alcuna formattazione grafica (Clean Code).
 */
public class ChallengeResult {
    private final boolean successo;
    private final String messaggioLogico;
    private final int valoreOttenuto; // Può essere il tiro del dado o il karma
    private final String entitaCoinvolta; // Nome del boss o dell'indizio

    public ChallengeResult(boolean successo, String messaggioLogico, int valoreOttenuto, String entitaCoinvolta) {
        this.successo = successo;
        this.messaggioLogico = messaggioLogico;
        this.valoreOttenuto = valoreOttenuto;
        this.entitaCoinvolta = entitaCoinvolta;
    }

    public boolean isSuccesso() { return successo; }
    public String getMessaggioLogico() { return messaggioLogico; }
    public int getValoreOttenuto() { return valoreOttenuto; }
    public String getEntitaCoinvolta() { return entitaCoinvolta; }
}