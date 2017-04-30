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
        
        
        var LIGHT_URL = "\(IP_ADDR)/api/\(USERNAME)/lights/\(LIGHT_ID)"
        
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
//        HUEStateChange (url: BRIGHT_URL, body: ["bri":lightBrightness])
        Alamofire.request(
            URL(string: BRIGHT_URL)!,
            method: .put,
            parameters: ["bri":lightBrightness],
            encoding: JSONEncoding.default).responseJSON { response in
                print(response)
        }
        self.trueBrightness = lightBrightness
    }
    
//    func HUEStateChange(url:String, body:[String: Any]) {
//        Alamofire.request(
//            URL(string: url)!,
//            method: .put,
//            parameters: body,
//            encoding: JSONEncoding.default).responseJSON { response in
//                print(response)
//        }
//    }
}
