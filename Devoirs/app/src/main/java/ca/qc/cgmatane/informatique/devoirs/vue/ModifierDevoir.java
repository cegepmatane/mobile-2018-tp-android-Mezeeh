package ca.qc.cgmatane.informatique.devoirs.vue;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.*;
import ca.qc.cgmatane.informatique.devoirs.R;
import ca.qc.cgmatane.informatique.devoirs.accesseur.DevoirsDAO;
import ca.qc.cgmatane.informatique.devoirs.modele.Devoir;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ModifierDevoir extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    protected DevoirsDAO accesseurDevoirs;
    protected Devoir devoir;

    protected EditText champMatiere,
                        champTache;

    protected Button actionModifierDevoir,
                        actionAjouterAlarme;

    protected Calendar calendrier;

    protected DatePickerDialog dialogueChoixDate;

    protected TimePickerDialog dialogueChoixHeure;

    protected TextView champAlarme;

    protected int anneeAlarme,
                    moisAlarme,
                    jourAlarme,
                    heureAlarme,
                    minuteAlarme;

    protected boolean aAlarme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vue_modifier_devoir);

        this.accesseurDevoirs = DevoirsDAO.getInstance();

        Bundle parametres = this.getIntent().getExtras();
        String parametre_id_devoir = parametres.get("id_devoir").toString();
        int id_livre = Integer.parseInt(parametre_id_devoir);

        devoir = accesseurDevoirs.trouverDevoir(id_livre);

        this.aAlarme = devoir.isaAlarme();

        champMatiere = findViewById(R.id.vue_modifier_devoir_champ_matiere);
        champTache = findViewById(R.id.vue_modifier_devoir_champ_tache);

        champMatiere.setText(devoir.getMatiere());
        champTache.setText(devoir.getTache());

        champAlarme = findViewById(R.id.vue_modifier_devoir_temps_alarme);
        aAlarme = devoir.isaAlarme();
        if(aAlarme) {
            Log.d("Hello", "" + devoir.getTempsAlarme());
            /*heureAlarme = devoir.getHeureAlarme();
            minuteAlarme = devoir.getMinuteAlarme();
            jourAlarme = devoir.getJourAlarme();
            moisAlarme = devoir.getMoisAlarme();
            anneeAlarme = devoir.getAnneeAlarme();*/
            champAlarme.setText("Alarme : " + new SimpleDateFormat("hh:mm dd/MM/yyyy").format(new Date((long) (devoir.getTempsAlarme() * 1000))));
        }

        actionModifierDevoir = findViewById(R.id.action_modifier_devoir);
        actionModifierDevoir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifierDevoir();
            }
        });

        actionAjouterAlarme = findViewById(R.id.action_modifier_devoir_ajouter_alarme);
        if(aAlarme)
            actionAjouterAlarme.setText("Modifier l'alarme");
        actionAjouterAlarme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                afficherDialogueChoixDate();
            }
        });
    }

    private void modifierDevoir(){
        devoir = new Devoir(champMatiere.getText().toString(), champTache.getText().toString(), this.devoir.getId_devoir());
        devoir.setaAlarme(aAlarme);
        if(aAlarme){
            try {
                long tempsAlarme = new SimpleDateFormat("hh:mm dd/MM/yyyy").parse(this.heureAlarme + ":" + this.minuteAlarme + " " + this.jourAlarme + "/" + this.moisAlarme + "/" + this.anneeAlarme).getTime();
                devoir.setTempsAlarme(tempsAlarme);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        accesseurDevoirs.modifierDevoir(devoir);
        naviguerRetourALaBibliotheque();
    }

    public void naviguerRetourALaBibliotheque(){
        this.finish();
    }

    @Override
    public void onDateSet(DatePicker view, int annee, int mois, int jourDuMois) {
        this.anneeAlarme = annee;
        this.moisAlarme = mois;
        this.jourAlarme = jourDuMois;

        afficherDialogueChoixHeure();
    }

    private void afficherDialogueChoixDate() {
        calendrier = Calendar.getInstance();

        int annee = calendrier.get(Calendar.YEAR);
        int mois = calendrier.get(Calendar.MONTH);
        int jour = calendrier.get(Calendar.DAY_OF_MONTH);

        dialogueChoixDate = new DatePickerDialog(ModifierDevoir.this, ModifierDevoir.this, annee, mois, jour);
        dialogueChoixDate.show();
    }

    private void afficherDialogueChoixHeure() {
        int heure = calendrier.get(Calendar.HOUR_OF_DAY);
        int minute = calendrier.get(Calendar.MINUTE);

        dialogueChoixHeure = new TimePickerDialog(ModifierDevoir.this, ModifierDevoir.this, heure, minute, DateFormat.is24HourFormat(this));
        dialogueChoixHeure.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int heureDuJour, int minute) {
        this.heureAlarme = heureDuJour;
        this.minuteAlarme = minute;

        Log.d("HELLO", "" + heureAlarme + " " + minuteAlarme);

        remplissageChampAlarme();
    }

    private void remplissageChampAlarme() {
        aAlarme = true;
        actionAjouterAlarme.setText("Modifier l'alarme");
        champAlarme.setText("Alarme : " + heureAlarme + ":" + minuteAlarme + " " + jourAlarme + "/" + moisAlarme + "/" + anneeAlarme);
    }
}
