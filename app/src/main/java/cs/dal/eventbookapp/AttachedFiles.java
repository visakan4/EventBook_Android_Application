package cs.dal.eventbookapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.pdf.PrintedPdfDocument;
import android.support.annotation.NonNull;

import android.support.design.widget.Snackbar;
import android.support.v4.os.EnvironmentCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.eventbookapp.R;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.drive.metadata.CustomPropertyKey;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import DataProvider.AttachedFileItemAdaper;
import DataProvider.ServiceDataProvider;
import Models.MetadataFile;
import Models.PDFHeader;
import Models.SummaryModel;

import static cs.dal.eventbookapp.AttachNotes.CustomPropertyName;
import static cs.dal.eventbookapp.AttachNotes.currentEventId;


public class AttachedFiles extends AppCompatActivity {

    private AttachedFileItemAdaper attachedFileItemAdaper;
    private ArrayList<MetadataFile> masterAttachedFiles;
    private  ListView listView;
    private Button btnSummarize;
    private MetadataBuffer attachedFilesMetada;
    private static int CONTENT_OPENEER =500;
    private ArrayList<SummaryModel> summaryFiles =new ArrayList<SummaryModel>();
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attached_files);
        masterAttachedFiles = new ArrayList<>();
        context = this;

        listView = (ListView) findViewById(R.id.attachedFilesListView);
        attachedFileItemAdaper = new AttachedFileItemAdaper(this,R.layout.row_list_attached_files);
        listView.setAdapter(attachedFileItemAdaper);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MetadataFile file  = (MetadataFile) view.getTag();
                OpenFileAttachedFile(file.getMetadata());

            }
        });

        findViewById(R.id.btnClosed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSummarize = (Button) findViewById(R.id.btnSummarize);
        btnSummarize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SummarizeToPdfFiles();
            }
        });
        btnSummarize.setVisibility(View.GONE);
        RetrieveDriveFileForCurrentEvent();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.attachedfilesmenu, menu);

        MenuItem menuItem = (MenuItem) menu.findItem(R.id.attachFileItemSearch);
         SearchView sView = (SearchView) MenuItemCompat.getActionView(menuItem);

        sView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                FilterAttachedFiles(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.attachFileItemSearch)
        {

        }else if(item.getItemId()==R.id.attachedFilesDelete)
        {
            DeleteCheckedFiles();

        }



        return super.onOptionsItemSelected(item);
    }

    private void RefreshFilesItem()
    {
        AsyncTask<Void,Void,Void> refreshFiles=  new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                if(attachedFileItemAdaper==null) return null;

                if(attachedFilesMetada!=null)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            attachedFileItemAdaper.clear();
                            btnSummarize.setVisibility(View.VISIBLE);
                            int attachFileCount = attachedFilesMetada.getCount();

                            for(int counter=0;counter<attachFileCount;counter++)
                            {
                                masterAttachedFiles.add(new MetadataFile(attachedFilesMetada.get(counter)));
                            }
                            attachedFileItemAdaper.addAll(masterAttachedFiles);
                            attachedFileItemAdaper.notifyDataSetChanged();
                        }
                    });
                }
                return null;
            }
        };

        refreshFiles.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private void DeleteCheckedFiles()
    {
        if(attachedFileItemAdaper==null) {
            return;
        }
        int fileTodDelete = attachedFileItemAdaper.getCount();
        for(int counter=0; counter< fileTodDelete;counter++)
        {
            if(attachedFileItemAdaper.getItem(counter).isMarkedToDelete()) {
                ExecuteTrash(attachedFileItemAdaper.getItem(counter));
            }
        }
    }

    private void ExecuteTrash(final MetadataFile metadataFile)
    {
        AsyncTask<Void,Void,Void> trashDelete = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if(AttachNotes.driveResourceClient!=null)
                {
                    DriveResource resource = metadataFile.getMetadata().getDriveId().asDriveResource();
                    AttachNotes.driveResourceClient.delete(resource)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            masterAttachedFiles.remove(metadataFile);
                                            attachedFileItemAdaper.clear();
                                            attachedFileItemAdaper.addAll(masterAttachedFiles);
                                            attachedFileItemAdaper.notifyDataSetChanged();
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                }
                return null;
            }
        };

      runAsyncTask(trashDelete);
    }

    private void RetrieveDriveFileForCurrentEvent()
    {

        AsyncTask<Void,Void,Void> getAttachedFiles = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                Query query = new Query.Builder().addFilter(Filters.and(Filters.contains(SearchableField.TITLE,AttachNotes.fileTitlePrefix),
                        Filters.eq(new CustomPropertyKey(CustomPropertyName,CustomPropertyKey.PUBLIC), AttachNotes.currentEventId)))
                        .build();

                com.google.android.gms.tasks.Task<MetadataBuffer> queryTask = AttachNotes.driveResourceClient.query(query);

                queryTask.continueWith(new Continuation<MetadataBuffer, Void>() {
                    @Override
                    public Void then(@NonNull com.google.android.gms.tasks.Task<MetadataBuffer> task) throws Exception {
                        attachedFilesMetada = task.getResult();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                RefreshFilesItem();
                            }
                        });

                        return null;
                    }
                });


                return null;
            }
        };
        runAsyncTask(getAttachedFiles);
    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    private void FilterAttachedFiles(String filterText)
    {
        if(masterAttachedFiles==null) return;
        ArrayList<MetadataFile> filteredFiles = new ArrayList<>();
        int fileCount = masterAttachedFiles.size();
        for(int counter=0;counter<fileCount;counter++)
        {
            if(masterAttachedFiles.get(counter).getMetadata().getTitle().contains(filterText)){
            filteredFiles.add(masterAttachedFiles.get(counter));
            }
        }
        attachedFileItemAdaper.clear();
        attachedFileItemAdaper.addAll(filteredFiles);
        attachedFileItemAdaper.notifyDataSetChanged();

    }

    private Task<Void> OpenAttachedFiles(Metadata metadata)
    {
        OpenFileActivityOptions openFileActivityOptions = new OpenFileActivityOptions.Builder()
                .setActivityTitle(metadata.getTitle())
                .setMimeType(Arrays.asList(metadata.getMimeType()))
                .setActivityStartFolder(metadata.getDriveId()).build();

        return AttachNotes.driveClient.newOpenFileActivityIntentSender(openFileActivityOptions)
                .continueWith(
                        new Continuation<IntentSender, Void>() {
                            @Override
                            public Void then(@NonNull Task<IntentSender> task) throws Exception {
                                startIntentSenderForResult(task.getResult(), CONTENT_OPENEER, null, 0, 0, 0);
                                    return null;
                            }
                        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if(requestCode==CONTENT_OPENEER && RESULT_OK == resultCode)
        {
            return;
        }
    }

    private void OpenFileAttachedFile(final Metadata metadata){


        Task<DriveContents> fileContentsTask = AttachNotes.driveResourceClient.openFile(metadata.getDriveId().asDriveFile(), DriveFile.MODE_READ_ONLY);
        fileContentsTask.continueWith(
                new Continuation<DriveContents, Void>() {
                    @Override
                    public Void then(@NonNull Task<DriveContents> task) throws Exception {

                        DriveContents contents = task.getResult();
                        if(contents==null) return null;
                        if(metadata.getMimeType().contains("text"))
                        {
                            ShowFileInsideAlertDialog(StreamTOString(contents.getInputStream()));
                        }else if(metadata.getMimeType().contains("image"))
                        {
                            ImageView imgView = new ImageView(AttachedFiles.this);
                            Bitmap bitMap  = BitmapFactory.decodeStream(contents.getInputStream());
                            imgView.setImageBitmap(bitMap);
                            imgView.setMinimumWidth((getWindowManager().getDefaultDisplay().getWidth()*4)/5);
                            imgView.setMinimumHeight((getWindowManager().getDefaultDisplay().getWidth()*4)/5);
                            ShowFileInsideAlertDialog(imgView);
                        }else if(metadata.getMimeType().contains("pdf")) {

                            String filePath = context.getFilesDir().getPath().toString() + "/EventBookSummary.pdf";
                            File newFile = new File(filePath);
                            try {
                                OutputStream outputStream =new  FileOutputStream(newFile);
                                byte[] buffer = new byte[1024];
                                InputStream inputStream = contents.getInputStream();
                                int len;
                                while ((len = inputStream.read(buffer)) != -1) {
                                    outputStream.write(buffer, 0, len);
                                }
                                inputStream.close();
                                outputStream.flush();
                                outputStream.close();

                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(newFile), "application/pdf");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                Intent intentChooser = Intent.createChooser(intent, "Open File");
                                startActivity(intentChooser);
                            }catch(Exception e)
                            {
                                e.printStackTrace();
                            }

                        }

                        return  null;
                    }
                });
    }

    private void ShowFileInsideAlertDialog(String  text)
    {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("EventBook");
        alertDialog.setMessage(text);
        alertDialog.setCancelable(false)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    private void ShowFileInsideAlertDialog(ImageView  imageView)
    {
        Dialog builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public String StreamTOString(InputStream inputStream)
    {
        StringBuilder builder = new StringBuilder();
        String line;
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        }catch (Exception ex)
        {
            return  "";
        }
    }

    private void SummarizeToPdfFiles()
    {
        summaryFiles.clear();
        ArrayList<Task<DriveContents>> tasks = new ArrayList<Task<DriveContents>>();
        for(int counter=0; counter<masterAttachedFiles.size();counter++)
        {
            final Metadata metadata = masterAttachedFiles.get(counter).getMetadata();
            if(metadata.getMimeType().contains("pdf")) {continue;}
            Task<DriveContents> fileContentsTask = AttachNotes.driveResourceClient.openFile(metadata.getDriveId().asDriveFile(), DriveFile.MODE_READ_ONLY)
                    .continueWithTask(new Continuation<DriveContents, Task<DriveContents>>() {
                        @Override
                        public Task<DriveContents> then(@NonNull Task<DriveContents> task) throws Exception {
                            DriveContents contents = task.getResult();
                            SummaryModel summaryOne = new SummaryModel();
                            summaryOne.driveContents = contents;
                            summaryOne.metadata = metadata;
                            summaryFiles.add(summaryOne);
                            return null;
                        }
                    });
            tasks.add(fileContentsTask);
        }

        Tasks.whenAll(tasks).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ProcessPdf();
            }
        });

    }

    private void ProcessPdf()
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            //Create a new Pdf Document Page
            Document doc = new Document(PageSize.A4,40,40,40,40);
            PdfWriter writer = PdfWriter.getInstance(doc,byteArrayOutputStream);
            writer.setPageEvent(new PDFHeader());
            doc.open();
            doc.addCreationDate();doc.addAuthor(ServiceDataProvider.getGlobalInstance().getUserName());
            doc.addSubject("Event Book Application Generator");

            if(summaryFiles!=null && summaryFiles.size()>0)
            {
                int count = summaryFiles.size();
                for(int counter=0;counter<count;counter++)
                {
                    DriveContents currentFile = summaryFiles.get(counter).driveContents;
                    Metadata metadata = summaryFiles.get(counter).metadata;
                    //If it is text
                    if(metadata.getMimeType().contains("text"))
                    {
                        doc.add(new Paragraph("Notes Added on" + metadata.getCreatedDate().toString()));
                        doc.add(new Paragraph(StreamTOString(currentFile.getInputStream())));

                    }else if(metadata.getMimeType().contains("image")){
                        doc.add(new Paragraph("Image Attached on" + metadata.getCreatedDate().toString())); ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        ByteArrayOutputStream imageStream  = new ByteArrayOutputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(currentFile.getInputStream());
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , imageStream);
                        Image myImg = Image.getInstance(imageStream.toByteArray());
                        myImg.setAlignment(Image.MIDDLE);
                        doc.add(myImg);
                    }
                    doc.add(new Paragraph("--------------------------------------------------------------"));
                }
            }


            doc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        SaveAsPDFFile(byteArrayOutputStream);

    }

    private void SaveAsPDFFile(final ByteArrayOutputStream  outputStream)
    {
        AsyncTask<Void,Void,Void> textSaveTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                final Task<DriveFolder> rootFolder = AttachNotes.driveResourceClient.getRootFolder();
                final Task<DriveContents> contentsTask = AttachNotes.driveResourceClient.createContents();

                Tasks.whenAll(rootFolder,contentsTask).continueWith(new Continuation<Void,Task<DriveFile>>() {
                    @Override
                    public Task<DriveFile> then(@NonNull Task<Void> task) throws Exception {

                        DriveFolder parentFolder = rootFolder.getResult();
                        DriveContents contents = contentsTask.getResult();
                        OutputStream outStream = contents.getOutputStream();
                        try{
                            outStream.write(outputStream.toByteArray());
                            outputStream.close();
                            outStream.close();
                        }catch (Exception io) {}

                        CustomPropertyKey eventID =
                                new CustomPropertyKey(CustomPropertyName, CustomPropertyKey.PUBLIC);

                        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder().setMimeType("application/pdf")
                                .setTitle( "EventBook"+ getCurrentTimeStamp()+".pdf").setCustomProperty(eventID,AttachNotes.currentEventId)
                                .build();

                        return AttachNotes.driveResourceClient.createFile(parentFolder,metadataChangeSet,contents).addOnCompleteListener(new OnCompleteListener<DriveFile>() {
                            @Override
                            public void onComplete(@NonNull Task<DriveFile> task) {
                                Toast.makeText(AttachedFiles.this,"Done",Toast.LENGTH_SHORT);
                            }
                        });
                    }
                });

                return null;
            }
        };

        runAsyncTask(textSaveTask);

    }

    private String getCurrentTimeStamp()
    {
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
    }

}


