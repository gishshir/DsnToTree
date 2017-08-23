package fr.tsadeo.app.dsntotree.service;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import fr.tsadeo.app.dsntotree.model.BlocTree;
import fr.tsadeo.app.dsntotree.model.CardinaliteEnum;
import fr.tsadeo.app.dsntotree.model.Dsn;
import fr.tsadeo.app.dsntotree.model.ErrorMessage;
import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.util.IConstants;
import fr.tsadeo.app.dsntotree.util.IJsonConstants;
import fr.tsadeo.app.dsntotree.util.JsonUtils;

public class AbstractReadDsn implements IConstants, IJsonConstants {

    protected final JsonUtils jsonUtils = new JsonUtils();
    protected final DsnService dsnService = ServiceFactory.getDsnService();

    protected ItemBloc getOrBuildItemBlocErreur(Dsn dsn) {

        ItemBloc itemBlocError = dsn.getItemBlocError();
        if (itemBlocError == null) {
            itemBlocError = new ItemBloc(0, null, "ERROR");
            itemBlocError.setErrorMessage(new ErrorMessage("Bloc d'erreurs"));
            dsn.setItemBlocError(itemBlocError);

        }
        return itemBlocError;
    }

    /*
     * Description de la structure arborescente de la DSN (phase/nature)
     */
    protected BlocTree buildRootTree(InputStream enteteInputStream, InputStream jsonInputStream) {

        if (enteteInputStream == null || jsonInputStream == null) {
            return null;
        }
        BlocTree treeBlocs = new BlocTree();

        try {
            JSONObject json = new JSONObject(IOUtils.toString(enteteInputStream, Charset.forName(UTF8)));
            enteteInputStream.close();
            jsonToBlocTree(treeBlocs, json);

            BlocTree bloc05 = treeBlocs.findChild(BLOC_05, true);
            if (bloc05 != null) {
                json = new JSONObject(IOUtils.toString(jsonInputStream, Charset.forName(UTF8)));
                jsonInputStream.close();
                jsonToBlocTree(bloc05, json);

            }
        } catch (Exception ex) {
            System.out.println("Echec lors de la lecture du fichier json");
            throw new RuntimeException(ex.getMessage());
        }
        return treeBlocs;
    }

    /*
     * Methode recursive Construction d'un bloc fils à partir de jsonObject et
     * rattachement à son parent
     */
    private void jsonToBlocTree(BlocTree blocTreeParent, JSONObject jsonObject) throws JSONException {

        if (jsonObject != null) {

            String blocLabel = null;
            if (jsonObject.has(IJsonConstants.JSON_BLOC) && !jsonObject.isNull(JSON_BLOC)) {
                blocLabel = jsonObject.getString(JSON_BLOC);
            }
            String cardinalite = null;
            if (jsonObject.has(IJsonConstants.JSON_CARDINALITE)
                    && !jsonObject.isNull(IJsonConstants.JSON_CARDINALITE)) {
                cardinalite = jsonObject.getString(JSON_CARDINALITE);
            }

            boolean actif = false;
            if (jsonObject.has(IJsonConstants.JSON_ACTIF) && !jsonObject.isNull(JSON_ACTIF)) {
                actif = jsonObject.getBoolean(JSON_ACTIF);
            }

            JSONArray sousBlocs = null;
            if (jsonObject.has(IJsonConstants.JSON_SOUS_BLOCS)) {
                sousBlocs = jsonObject.getJSONArray(JSON_SOUS_BLOCS);
            }

            BlocTree blocDependencies = null;

            if (blocTreeParent.getBlocLabel() == null) {

                blocDependencies = blocTreeParent;
                blocDependencies.setBlocLabel(blocLabel);
                blocDependencies.setActif(actif);
                blocDependencies.setCardinalite(this.getCardinalite(cardinalite));
            }

            else if (blocLabel != null && blocLabel.isEmpty()) {
                blocDependencies = blocTreeParent;
            } else {
                blocDependencies = new BlocTree();
                blocDependencies.setBlocLabel(blocLabel);
                blocDependencies.setActif(actif);
                blocDependencies.setCardinalite(this.getCardinalite(cardinalite));
            }

            if (blocTreeParent != blocDependencies) {
                blocTreeParent.addChild(blocDependencies);
            }

            if (sousBlocs != null) {

                for (int i = 0; i < sousBlocs.length(); i++) {
                    JSONObject sousBloc = sousBlocs.getJSONObject(i);
                    jsonToBlocTree(blocDependencies, sousBloc);
                }

            }
        }
    }

    private CardinaliteEnum getCardinalite(String cardinalite) {

        CardinaliteEnum result;
        try {

            result = CardinaliteEnum.valueOf(cardinalite);
        } catch (Exception e) {
            result = CardinaliteEnum.UN;
        }

        return result;
    }

}
