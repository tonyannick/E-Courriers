package dcsibudget.alfresco;

import dcsibudget.fileManager.PropertiesFilesReader;
import dcsibudget.logsManager.LoggerCreator;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.primefaces.shaded.json.JSONObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ConnexionAlfresco implements Serializable {

    private static final long serialVersionUID = 3355915762235233279L;
    private static String idDocument;
    public static String mimeDocument;
    private String property = "java.io.tmpdir";
    private static StreamedContent streamedContent;
    private static Logger ConnexionAlfrescoLogger = LoggerCreator.creerUnLog("ConnexionAlfresco");

    public static String uploadDocument(String authTicket, File fileobj, String filename, String filetype, String description, String dossierCible) {
        PropertiesFilesReader.lireLeFichierDeProprietesDAlfresco("alfrescoURLs.properties");
        try {
            String urlString = PropertiesFilesReader.alfrescoTicketUrl+authTicket;
            HttpClient client = new HttpClient();
            PostMethod mPost = new PostMethod(urlString);
            Part[] parts = {
                    new FilePart("filedata", filename, fileobj, filetype, null),
                    new StringPart("filename", filename),
                    new StringPart("description", description),
                    //new StringPart("destination", destination)
                    new StringPart("siteid", "sitebudget"),
                    new StringPart("containerid", "documentLibrary"),
                    new StringPart("uploaddirectory", dossierCible)
            };

            mPost.setRequestEntity(
                    new MultipartRequestEntity(parts, mPost.getParams())
            );

            int statusCode1 = client.executeMethod(mPost);
            System.out.println("statusLine>>>" + statusCode1 + "......" + mPost.getStatusLine() + mPost.getResponseBodyAsString());
            mPost.releaseConnection();
            if (statusCode1 == HttpStatus.SC_OK) {
                String premiereCoupure = mPost.getResponseBodyAsString().substring(mPost.getResponseBodyAsString().indexOf("SpacesStore/"));
                String deuxiemeCoupure = premiereCoupure.substring(0,premiereCoupure.indexOf("\""));

                if (deuxiemeCoupure.contains("[") || deuxiemeCoupure.contains("]")){
                    deuxiemeCoupure = deuxiemeCoupure.replace("[","");
                }
                if ( deuxiemeCoupure.contains("]")){
                    deuxiemeCoupure = deuxiemeCoupure.replace("]","");
                }
                idDocument = deuxiemeCoupure;
            }else{
                System.out.println("Bad");
                LoggerCreator.definirMessageAlerte(ConnexionAlfrescoLogger,"Erreur de connexion Alfresco: "+statusCode1);
            }
        } catch (Exception e) {
            LoggerCreator.definirMessageErreur(ConnexionAlfrescoLogger,"Erreur de connexion Alfresco: "+e);
            e.printStackTrace();
        }

        return idDocument;

    }

    public static String enregistrerFichierCourrierDansAlfresco(File fichierAEnvoyer, String typeDeFichier,String dossierCible){

        String resultat = null;
        //try {
            resultat = uploadDocument(getAlfticket(), fichierAEnvoyer,fichierAEnvoyer.getName(),typeDeFichier,"description", dossierCible);
       // } catch (IOException e) {
        //    e.printStackTrace();
        ///}
    
        return  resultat;
    }

    public static StreamedContent telechargerDocumentDansAlfresco(String idDocument) {
        PropertiesFilesReader.lireLeFichierDeProprietesDAlfresco("alfrescoURLs.properties");
        byte[] bytesFromInputStream = null;
        String urlComplete = null;
        URL url = null;
        URLConnection urlConnection = null;
        InputStream inputStream = null;

        try {

            urlComplete = PropertiesFilesReader.alfresscoDownloadFileUrl + idDocument +"?a=false&alf_ticket="+getAlfticket();
            url = new URL(urlComplete);
            urlConnection = url.openConnection();
            if(urlConnection != null){
                if(urlConnection.getContentType() == null){
                    mimeDocument = "application/octet-stream";
                }else{
                    mimeDocument = urlConnection.getContentType( );
                }
                inputStream = urlConnection.getInputStream();
                bytesFromInputStream = IOUtils.toByteArray(inputStream);/*TODO methode depreciée à gerer*/
                streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(bytesFromInputStream),"application/pdf");
            }else{
                System.out.println("mal bad ");
                LoggerCreator.definirMessageAlerte(ConnexionAlfrescoLogger,"Erreur de telechargement dans Alfresco: ");
            }

        } catch (MalformedURLException e) {
            LoggerCreator.definirMessageErreur(ConnexionAlfrescoLogger,"Erreur de téléchargement (URL) Alfresco : "+e);
            e.printStackTrace();
        } catch (IOException io){
            LoggerCreator.definirMessageErreur(ConnexionAlfrescoLogger,"Erreur de téléchargement Alfresco : "+io);
            io.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return streamedContent;

    }

    public static void deleteDocumentInAlfresco(String idDocument){
        try {
            URL url = new URL(URLAlfresco.alfresscoDeletedFileUrl + idDocument + "?alf_ticket="+getAlfticket());
            /*CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpDelete httpDelete = new HttpDelete(url.toString());
            HttpResponse httpResponse = httpClient.execute(httpDelete);

            BufferedReader br = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + httpResponse.getStatusLine().getStatusCode());
            }
            //Create the StringBuffer object and store the response into it.
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                System.out.println("Response : \n"+result.append(line));
            }*/
            HttpClient httpClient = new HttpClient();
            DeleteMethod deleteMethod = new DeleteMethod(url.toString());

            int requestResponse = httpClient.executeMethod(deleteMethod);
            System.out.println("url " +url.toString());
            System.out.println("statusLine>>>" +deleteMethod.getStatusLine()+"<<<<"+deleteMethod.getResponseBodyAsString());

            if(requestResponse == HttpStatus.SC_OK){
                System.out.println("Good deleting");
            }else{
                System.out.println("Bad");
            }

           /* final URIBuilder uriBuilder = new URIBuilder(URLAlfresco.alfresscoDeletedFileUrl + idDocument );
            uriBuilder.addParameter("alf_ticket", getAlfticket());

            final CloseableHttpClient httpclient = HttpClientBuilder.create().build();
            final HttpDelete httpDelete = new HttpDelete(uriBuilder.toString());
            final HttpResponse httpResp = httpclient.execute(httpDelete);
            final StatusLine status = httpResp.getStatusLine();
            final int statusCode = status.getStatusCode();
            final String statusMsg = status.getReasonPhrase();
            System.out.println("Status: "+statusCode +" | "+ statusMsg);*/



            // deleteMethod.releaseConnection();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String getAlfticket()  {
        PropertiesFilesReader.lireLeFichierDeProprietesDAlfresco("alfrescoURLs.properties");
        try {
            URL url = new URL(PropertiesFilesReader.alfrescoUploadFileUrl);
            URLConnection con = null;
            con = url.openConnection();
            if(con.getInputStream() != null){
                InputStream in = con.getInputStream();
                String encoding = con.getContentEncoding();
                encoding = encoding == null ? "UTF-8" : encoding;
                String json = IOUtils.toString(in, encoding);
                JSONObject getData = new JSONObject(json);
                return getData.getJSONObject("data").get("ticket").toString();
            }else{
                return null;
            }
        } catch (IOException e) {
            LoggerCreator.definirMessageErreur(ConnexionAlfrescoLogger,"Erreur Alfresco : "+e);
            e.printStackTrace();
            return null;
        }
    }
}
