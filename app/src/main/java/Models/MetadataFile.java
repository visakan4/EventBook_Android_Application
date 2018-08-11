package Models;

import com.google.android.gms.drive.Metadata;

/**
 * Created by jebes on 11/29/2017.
 */


/**
 * Class MetadataFile - Model class that acts like a wrappper for Metadata and its associated file content
 */
public class MetadataFile {


    public Metadata getMetadata() {
        return metadata;
    }

    public boolean isMarkedToDelete() {
        return markedToDelete;
    }


    /**
     * Method: SetMarkedToDelete a setter used to mark a particular metadata file as deleted
     * @param markedToDelete
     */
    public void setMarkedToDelete(boolean markedToDelete) {
        this.markedToDelete = markedToDelete;
    }

    private boolean markedToDelete;
    private Metadata metadata;

    /**
     * Constructor that takes in the Metadata object as input
     * @param metadataInput
     */
    public MetadataFile(Metadata metadataInput)
    {
        this.metadata = metadataInput;
        this.markedToDelete = false;
    }

}
