package fr.tsadeo.app.dsntotree.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import fr.tsadeo.app.dsntotree.model.Dsn;
import fr.tsadeo.app.dsntotree.model.ItemRubrique;
import fr.tsadeo.app.dsntotree.util.IConstants;

public class WriteDsn implements IConstants {

    private final DsnService dsnService = new DsnService();

    // Ecrit le fichier Dsn au meme endroit
    // nom fichier = <nom fichier dsn>_<timestamp>
    public File write(Dsn dsn) {

        if (dsn == null || dsn.getFile() == null || dsn.getRubriques() == null || dsn.getRubriques().isEmpty()) {
            throw new RuntimeException("Dsn invalide!");
        }

        // rubrique portant le numero de ligne
		ItemRubrique itemRubrique = this.dsnService.findOneRubrique(dsn.getRubriques(), BLOC_90, RUB_001);
		if (itemRubrique != null) {
			itemRubrique.setValue(Integer.toString(dsn.getRubriques().size()));
			itemRubrique.setModified(true);
		}

        return this.writeDsnFile(dsn.getRubriques(), this.getDsnSaveFile(dsn));

    }

    private File writeDsnFile(List<ItemRubrique> listRubriques, File file) {

        List<String> lines = new ArrayList<String>();
        int index = 1;
        for (ItemRubrique itemRubrique : listRubriques) {
            String line = this.dsnService.getRubriqueLine(itemRubrique);
            System.out.println(index++ + " :" +line);
            lines.add(line);
        }

        OutputStream os = null;

        try {

            os = new FileOutputStream(file);
            IOUtils.writeLines(lines, null, os, UTF8);

        } catch (Exception e) {
            throw new RuntimeException("Impossible de sauvegarder le fichier: " + e.getMessage());
        } finally {
            IOUtils.closeQuietly(os);
        }

        return file;

    }

    private File getDsnSaveFile(Dsn dsn) {

        return dsn.getFile();
    }

}
