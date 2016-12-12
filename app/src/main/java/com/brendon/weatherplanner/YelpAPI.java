package com.brendon.weatherplanner;

/**
 * Created by Brendon on 12/12/16.
 */

public class YelpAPI {


    class Token
    {
        private String expires_in;

        private String token_type;

        private String access_token;

        public String getExpires_in ()
        {
            return expires_in;
        }

        public void setExpires_in (String expires_in)
        {
            this.expires_in = expires_in;
        }

        public String getToken_type ()
        {
            return token_type;
        }

        public void setToken_type (String token_type)
        {
            this.token_type = token_type;
        }

        public String getAccess_token ()
        {
            return access_token;
        }

        public void setAccess_token (String access_token)
        {
            this.access_token = access_token;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [expires_in = "+expires_in+", token_type = "+token_type+", access_token = "+access_token+"]";
        }
    }



}
