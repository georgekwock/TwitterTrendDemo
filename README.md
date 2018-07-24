# TwitterTrendDemo
![default layout](https://github.com/georgekwock/TwitterTrendDemo/blob/master/pic.png)

1. Architectual model: MVVM
   Data-binding trend name, volume number
2. Http Request process:

AsyncTask : Google Location-------Twitter Authentication-------Token varification--------request Twitter Data------
Parse Json Data--------Show data in recyclerview
