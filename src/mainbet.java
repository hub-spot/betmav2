import java.io.IOException;
import java.util.Date;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.*;

public class mainbet {
    public static void main(final String[] args) throws Exception {
        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            // ID 94 is for Badminton
            final HttpGet httpget = new HttpGet("https://api.b365api.com/v2/events/upcoming?sport_id=94&token=94787-xDMG6Ah5Mqdzv6");

            System.out.println("Executing request " + httpget.getMethod() + " " + httpget.getUri());

            // Create a custom response handler
            final HttpClientResponseHandler<String> responseHandler = new HttpClientResponseHandler<String>() {

                @Override
                public String handleResponse(
                        final ClassicHttpResponse response) throws IOException {
                    final int status = response.getCode();
                    if (status >= HttpStatus.SC_SUCCESS && status < HttpStatus.SC_REDIRECTION) {
                        final HttpEntity entity = response.getEntity();
                        try {
                            return entity != null ? EntityUtils.toString(entity) : null;
                        } catch (final ParseException ex) {
                            throw new ClientProtocolException(ex);
                        }
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            final String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println("----------------------------------------");
            //System.out.println(responseBody);
            // http://theoryapp.com/parse-json-in-java/
            // Load complete Results from Betsapi
            // api doc=https://de.betsapi.com/docs/GLOSSARY.html#r-pager
            JSONObject obj = new JSONObject(responseBody);
            JSONObject res;
            JSONObject res2;
            Date eventTime = new Date ();
            String eventID="";
            String betsID="";
            String leagueName="";
            String homeTeamName="";
            String awayTeamName="";

            // Go over each object in results
            for (int i=0;i<=obj.getJSONArray("results").length()-1;i++) {
                // Init Data
                try {
                    res = obj.getJSONArray("results").getJSONObject(i);
                    eventID = res.getString("id");
                    betsID = res.getString("bet365_id");
                    eventTime.setTime((long)Integer.valueOf(res.getString("time"))*1000);

                    res2 = res.getJSONObject("league");
                    leagueName = res2.getString("name");
                    res2 = res.getJSONObject("home");
                    homeTeamName = res2.getString("name");
                    res2 = res.getJSONObject("away");
                    awayTeamName = res2.getString("name");
                    ;
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                // Print Data
                System.out.println("----- Event ----- " +i );
                System.out.println("Date: "+eventTime);
                System.out.println("EventID: "+eventID);
                System.out.println("bet365_id: "+betsID);
                System.out.println("League name: "+ leagueName);
                System.out.println("Home Team: "+ homeTeamName);
                System.out.println("Away Team: "+ awayTeamName);

            }
        }
    }
    /*public int checkSaveID(String eventID)
            throws IOException {
        String id = eventID;
        // Status 1= new and saved Status 2=already exists Status 3=?
        int status=0;
        BufferedWriter writer = new BufferedWriter(new FileWriter("id_history.txt"));
        writer.write(str);

        writer.close();
        return status;
    }*/
}
