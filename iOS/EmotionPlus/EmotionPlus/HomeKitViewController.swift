//
//  HomeKitViewController.swift
//  EmotionPlus
//
//  Created by Xiaozhe Wang on 4/28/17.
//  Copyright © 2017 iot17steam5. All rights reserved.
//


import UIKit
import Alamofire
import SwiftyJSON

class HomeKitViewController: UIViewController {
    
    var IP_ADDR = "http://10.0.21.248"
    var LIGHT_ID = "2"
    var USERNAME = "-rJnC9YCU0BdyWcpaJoHV4E9Yv3pL2hWPxQHbPPq"
    
    var EMOTION_SERVER = "http://172.29.92.105:8888"
    var RECOMMENDATION_SERVER = "http://172.29.93.218:3000"
    var MUSIC_SERVER = "http://172.29.93.218:8080"
    
    var EMOTION_URL = ""
    var MUSIC_LIST_URL = ""
    var HUE_URL = ""
    
    var PLAY_MUSIC_URL = ""
    var PAUSE_MUSIC_URL = ""
    var RESUME_MUSIC_URL = ""
    
    @IBOutlet weak var lightBrightness: UISlider!
    @IBOutlet weak var lightSwitch: UISwitch!
    var trueBrightness = 1
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        self.EMOTION_URL = "\(EMOTION_SERVER)/emotion"
        self.MUSIC_LIST_URL = "\(RECOMMENDATION_SERVER)/recommendation/music"
        self.HUE_URL = "\(RECOMMENDATION_SERVER)/hue"
        
        self.PLAY_MUSIC_URL = "\(MUSIC_SERVER)/music?song="
        self.PAUSE_MUSIC_URL = "\(MUSIC_SERVER)/music/pause"
        self.RESUME_MUSIC_URL = "\(MUSIC_SERVER)/music/resume"
        
        lightBrightness.minimumValue = 1
        lightBrightness.maximumValue = 254
        
        let LIGHT_URL = "\(IP_ADDR)/api/\(USERNAME)/lights/\(LIGHT_ID)"
        
        //         get HUE value
        Alamofire.request(
            URL(string: LIGHT_URL)!,
            method: .get).responseJSON { response in
                print(response.request)  // original URL request
                print(response.response) // HTTP URL response
                print(response.data)     // server data
                print(response.result)   // result of response serialization
                
                if let json = response.result.value {
                    let parsed = JSON(json)
                    print(parsed["state"]["on"])
                    print(parsed["state"]["bri"])
                    
                    self.lightSwitch.isOn = parsed["state"]["on"].boolValue
                    self.lightBrightness.value = parsed["state"]["bri"].floatValue
                    
                    self.trueBrightness = Int(self.lightBrightness!.value)
                }
        }
        
        self.Core()
        
        weak var timer: Timer?
        timer = Timer.scheduledTimer(withTimeInterval: 10.0, repeats: true) { [weak self] _ in
            // do something here
            self?.Core()
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    @IBAction func LightSwitch(_ sender: UISwitch) {
        let isLightOn = sender.isOn
        print("\(isLightOn)")
        
        let SWITCH_URL = "\(IP_ADDR)/api/\(USERNAME)/lights/\(LIGHT_ID)/state"
        //        var PARAMS = "{\"on\":\(isLightOn)}"
        //        print (PARAMS)
        
        // send isLightOn to HUE
        //        Alamofire.request(
        //            URL(string: "https://www.baidu.com")!,
        //            method: .get).responseString { response in
        //                print(response)
        //        }
        
        Alamofire.request(
            URL(string: SWITCH_URL)!,
            method: .put,
            parameters: ["on":isLightOn, "bri":self.trueBrightness],
            encoding: JSONEncoding.default).responseJSON { response in
                print(response)
        }
        
        //        Alamofire.request(
        //            URL(string: "https://www.facebook.com")!,
        //            method: .get).responseString { response in
        //                print(response)
        //        }
    }
    
    
    @IBAction func LightBrightnessChange(_ sender: UISlider) {
        let lightBrightness = Int(sender.value)
        if lightBrightness == self.trueBrightness {
            return
        }
        
        print(lightBrightness)
        let BRIGHT_URL = "\(IP_ADDR)/api/\(USERNAME)/lights/\(LIGHT_ID)/state"
        //        var PARAMS = "\"bri\":\(lightBrightness)"
        //        print (PARAMS)
        
        // send brightness to HUE
        Alamofire.request(
            URL(string: BRIGHT_URL)!,
            method: .put,
            parameters: ["bri":lightBrightness],
            encoding: JSONEncoding.default).responseJSON { response in
                print(response)
        }
        self.trueBrightness = lightBrightness
    }
    
    func PrintRes(response:Any) {
        print(response)
    }
    
//    @IBAction func test(_ sender: UIButton) {
//        self.Core()
//        
//        weak var timer: Timer?
//        timer = Timer.scheduledTimer(withTimeInterval: 10.0, repeats: true) { [weak self] _ in
//            // do something here
//            self?.Core()
//        }
//    }
    
    @IBOutlet weak var musicTitle: UILabel!
    @IBOutlet weak var musicSinger: UILabel!
    var musicList: JSON = []
    
    var oldEmotion: String = ""
    var EMOTIONS: [String] = ["sadness","neutral","contempt","disgust","anger","surprise","fear","happiness"]
    
    func Core () {
        print("Core")
        // GET emotion
        // after obtain the response from GET
        // POST emotion and get response of a list of music
        // POST emotion and get response of HUE light brightness

        print(EMOTION_URL)
        Alamofire.request(
            URL(string: EMOTION_URL)!,
            method: .get).responseJSON { response in
                print(response)
                if var json = response.result.value as? [String: Any] {
                    // emotion get!
                    print(json)
//                    json["anger"] = 0.0
//                    json["fear"] = 0.0
//                    json["surprise"] = 0.0
//                    json["contempt"] = 0.0
//                    json["disgust"] = 0.0
//                    json["happiness"] = 1.0
//                    json["neutral"] = 0.0
//                    json["sadness"] = 0.0
                    
                    var maxIdx = self.EMOTIONS[0]
                    for emotion in self.EMOTIONS {
                        let val = json[emotion]! as! Float
                        //                        print(val)
                        if val > (json[maxIdx]! as! Float) {
                            maxIdx = emotion
                        }
                    }
                    
                    if maxIdx != self.oldEmotion {
                        self.oldEmotion = maxIdx
                        
                        Alamofire.request(
                            URL(string: self.HUE_URL)!,
                            method: .post,
                            parameters: json,
                            encoding: JSONEncoding.default).responseString { response in
                                // hue get!
                                print ("HUE")
                                print(response.result.value)
                                
                                let newBrightness = Int(response.result.value!)!
                                print ("new brightness")
                                print (newBrightness)
                                let BRIGHT_URL = "\(self.IP_ADDR)/api/\(self.USERNAME)/lights/\(self.LIGHT_ID)/state"
                                
                                // send brightness to HUE
                                Alamofire.request(
                                    URL(string: BRIGHT_URL)!,
                                    method: .put,
                                    parameters: ["bri":newBrightness, "on":true],
                                    encoding: JSONEncoding.default).responseJSON { response in
                                        print(response)
                                }
                                self.trueBrightness = newBrightness
                                self.lightSwitch.isOn = true
                                self.lightBrightness.value = Float(newBrightness)
                                
                                
                        }
                        
                        Alamofire.request(
                            URL(string: self.MUSIC_LIST_URL)!,
                            method: .post,
                            parameters: json,
                            encoding: JSONEncoding.default).responseJSON { response in
                                // music list get!
                                print(response)
                                if let json = response.result.value{
                                    // music list json get!
                                    print("music json")
                                    print(json)
                                    //                                var tmp = JSON(json)
                                    //                                print("tmp")
                                    //                                print(tmp)
                                    self.musicList = JSON(json)
                                    self.music_index = 0
                                    self.ChangeMusic()
                                    self.StartPlayMusic()
                                    
                                    self.playState = 1
                                    let image = UIImage(named: "Pause")
                                    self.playPauseBtn.setImage(image, for: UIControlState.normal)
                                }
                        }
                    }
                    
                }
        }
    }
    
    func ChangeMusic () {
        print("change music")
        
        var music = musicList[self.music_index]
        print(music)
        var musicID = music["name"]
        var musicName = music["name"]
        var singer = music["singer"]
        var background = music["img"]
        var escaped_img_url = "http://\(background)".addingPercentEncoding(withAllowedCharacters: .urlPathAllowed)
        print(escaped_img_url!)
        
        self.musicTitle.text = "\(musicName)"
        self.musicSinger.text = "\(singer)"
        
        if let url = URL(string: escaped_img_url!) {
            if let data = try? Data(contentsOf: url) {
                let newimage = UIImage(data: data)
                self.musicBackground.image = newimage
                self.bigBgd.image = newimage
            }
        }
        
//        if let data = try? Data(contentsOf: URL(escaped_img_url!)) {
//            let newimage = UIImage(data: data)
//            self.musicBackground.image = newimage
//            self.bigBgd.image = newimage
//        }
        
//        self.musicBackground.downloadedFrom(link: escaped_img_url!)
//        self.bigBgd.downloadedFrom(link: escaped_img_url!)
        
        self.playState = 1
        let image = UIImage(named: "Pause")
        playPauseBtn.setImage(image, for: UIControlState.normal)
        StartPlayMusic()
    }
    
    func StartPlayMusic () {
        print("start new music")
        
//        for (_, music) in musicList {
        var music = musicList[self.music_index]
        print(music)
//            musicBackgroundImg.image how to set img src?
        let musicID = music["name"]
        let escapeMusicName = "\(musicID)".addingPercentEncoding(withAllowedCharacters: .urlPathAllowed)!
        let playMusicUrl = "\(PLAY_MUSIC_URL)\(escapeMusicName)"
        print(playMusicUrl)
        Alamofire.request(URL(string: playMusicUrl)!)
        
    }
    var playState = 0
    var music_index = -1
    
    @IBOutlet weak var bigBgd: UIImageView!
    @IBOutlet weak var musicBackground: UIImageView!
    @IBOutlet weak var playPauseBtn: UIButton!
    @IBAction func PlayPauseSwitch(_ sender: UIButton) {
//        var image
        if playState == 1 {
            let image = UIImage(named: "Play")
            sender.setImage(image, for: UIControlState.normal)
            playState = 0
            
            // pause the music
            Alamofire.request( URL(string: "\(PAUSE_MUSIC_URL)")!, method: .get)
        } else {
            let image = UIImage(named: "Pause")
            sender.setImage(image, for: UIControlState.normal)
            playState = 1
            // resume the music
            Alamofire.request( URL(string: "\(RESUME_MUSIC_URL)")!, method: .get)
        }
        
    }
    
    @IBAction func PrevBtnPrsd(_ sender: UIButton) {
        print("prev music")
        self.music_index = self.music_index - 1
        if (self.music_index < 0) {
            self.music_index = musicList.count - 1
        }
        ChangeMusic()
//        if playState == 1 {
//            StartPlayMusic()
//        }
    }
    
    @IBAction func NextBtnPrsd(_ sender: UIButton) {
        print("prev music")
        self.music_index = self.music_index + 1
        if (self.music_index >= musicList.count) {
            self.music_index = 0
        }
        ChangeMusic()
//        if playState == 1 {
//            StartPlayMusic()
//        }
    }
}

//func getDataFromUrl(url: URL, completion: @escaping (_ data: Data?, _  response: URLResponse?, _ error: Error?) -> Void) {
//    URLSession.shared.dataTask(with: url) {
//        (data, response, error) in
//        completion(data, response, error)
//        }.resume()
//}

//extension UIImageView {
//    func downloadedFrom(url: URL, contentMode mode: UIViewContentMode = .scaleAspectFit) {
//        contentMode = mode
//        URLSession.shared.dataTask(with: url) { (data, response, error) in
//            guard
//                let httpURLResponse = response as? HTTPURLResponse, httpURLResponse.statusCode == 200,
//                let mimeType = response?.mimeType, mimeType.hasPrefix("image"),
//                let data = data, error == nil,
//                let image = UIImage(data: data)
//                else { return }
//            DispatchQueue.main.async() { () -> Void in
//                self.image = image
//            }
//            }.resume()
//    }
//    func downloadedFrom(link: String, contentMode mode: UIViewContentMode = .scaleAspectFit) {
//        guard let url = URL(string: link) else { return }
//        downloadedFrom(url: url, contentMode: mode)
//    }
//}
