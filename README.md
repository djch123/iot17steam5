## EmotionPlus
This is a mobile application which helps people to manage their everyday emotions and provide personalized recommendations such as music, restaurants and sports based on the emotions as well as history preferences. Our app supports both Android and IOS. 

The app integrates Raspberry-PI, Hue lights and music players. Therefore, it highly reflects the concepts of [IoT](https://en.wikipedia.org/wiki/Internet_of_things). Besides, the app uses [Microsoft Emotion API](https://www.microsoft.com/cognitive-services/en-us/emotion-api) to analyze people's emotions. It's also worth mentioning that we build a server hosted on [AWS](https://aws.amazon.com/) to do emotion analysis as well as make recommendations. There are irreplaceable benefits of having an independent server instead of making requests in mobile app developent, i.e. Android project and IOS project. The advantages are as follow: 1. A server hosted on AWS can have much higher throughput and lower latency when huge amount of requests come; 2. The server is independent from mobile development thus avoiding duplicate code when implementing the same logic in Android and IOS; 3. It can enable better team work. 

## Acknowledgement
Thank to all the team members for your hardwork. 

[Here](https://drive.google.com/a/west.cmu.edu/file/d/0B2qvjfK0oR0yOUlqQzdEbG0tWDQ/view?usp=sharing) is the introduction slides for the app. 
