# instagram_rss
Convert an Instagram profile into an RSS feed.

## Prerequisites
- Java ≥ 7
- Servlet container like Tomcat ≥ 7, Jetty ≥ 8, or similar
- Maven 3

## Compiling
    $ git clone https://github.com/Aldaviva/instagram_rss.git
    $ cd instagram_rss
    $ mvn package
    
## Running
1. Copy `target\instagram_rss.war` to your servlet container's `webapps` directory, or however your server deploys WARs.

## Usage
1. Go to `http://127.0.0.1:8080/instagram_rss` in your browser (use your server's IP address).
2. Type in the username (`mad.dangerous`) or URL (`https://www.instagram.com/mad.dangerous`) of the profile to which you want to subscribe.
3. You will be redirected to the RSS feed for the profile. You can copy this URL to your favorite RSS news reader.
