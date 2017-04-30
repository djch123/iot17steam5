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
    
    var IP_ADDR = "http://192.168.1.119"
    var LIGHT_ID = "2"
    var USERNAME = "9qgwYPyqqt5TMW8BM2GuTtKCWe-dmqk81ezqQ3LO"
    
    @IBOutlet weak var lightBrightness: UISlider!
    @IBOutlet weak var lightSwitch: UISwitch!
    var trueBrightness = 1
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
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
    
    @IBAction func test(_ sender: UIButton) {
        Core()
    }
    
    @IBOutlet weak var musicBackgroundImg: UIImageView!
    @IBOutlet weak var musicTitle: UILabel!
    @IBOutlet weak var musicSinger: UILabel!
    var musicList: [String:String] = [:]
    
    func Core () {
        print("Core")
        // GET emotion
        // after obtain the response from GET
        // POST emotion and get response of a list of music
        // POST emotion and get response of HUE light brightness
        let EMOTION_SERVER = "http://172.29.92.105:8888"
        let RECOMMENDATION_SERVER = "2.2.2.2"
        
        let EMOTION_URL = "\(EMOTION_SERVER)/emotion"
        let MUSIC_LIST_URL = "\(RECOMMENDATION_SERVER)/recommendation/music"
        
//        let PAUSE_MUSIC_URL = "\(MUSIC_SERVER)/music/pause"
//        let RESUME_MUSIC_URL = "\(MUSIC_SERVER)/music/resume"
//        
        print(EMOTION_URL)
        Alamofire.request(
            URL(string: EMOTION_URL)!,
            method: .get).responseJSON { response in
                print(response)
                if let json = response.result.value as? [String: Any] {
                    // emotion get!
                    print(json)
                    
                    Alamofire.request(
                        URL(string: MUSIC_LIST_URL)!,
                        method: .post,
                        parameters: json,
                        encoding: JSONEncoding.default).responseJSON { response in
                            // music list get!
                            print(response)
                            if let json = response.result.value as? [String: String] {
                                // music list json get!
                                self.musicList = json
                                self.StartPlayMusic()
                            }
                    }
                }
        }
    }
    
    func StartPlayMusic () {
//        let MUSIC_SERVER = "3.3.3.3"
//        let PLAY_MUSIC_URL = "\(MUSIC_SERVER)/music?song="
//        
//        for music in musicList {
////            musicBackgroundImg.image how to set img src?
//            var musicID = music["id"]
//            self.musicTitle = music["title"]
//            self.musicSinger = music["singer"]
//            Alamofire.request(
//                URL(string: "\(PLAY_MUSIC_URL)\(musicID)"),
//                method: .get)
//        }
        
        self.playState = 1
    }
    var playState = 0
    
    @IBAction func PlayPauseSwitch(_ sender: UIButton) {
//        var image
        if playState == 1 {
            let image = UIImage(named: "Play")
            sender.setImage(image, for: UIControlState.normal)
            playState = 0
            // pause the music
        } else {
            let image = UIImage(named: "Pause")
            sender.setImage(image, for: UIControlState.normal)
            playState = 1
            // resume the music
        }
        
    }
}
