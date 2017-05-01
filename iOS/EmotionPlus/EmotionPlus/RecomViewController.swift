//
//  RecomViewController.swift
//  EmotionPlus
//
//  Created by Xiaozhe Wang on 4/28/17.
//  Copyright © 2017 iot17steam5. All rights reserved.
//

import UIKit
import SwiftyJSON
import Alamofire

class RecomViewController: UIViewController {
    
    var EMOTION_SERVER = "http://172.29.92.105:8888"
    var RECOMMENDATION_SERVER = "http://172.29.93.218:3000"
    
    var EMOTION_URL = ""
    var RESTAURANT_LIST_URL = ""
    var SPORT_LIST_URL = ""
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        self.EMOTION_URL = "\(EMOTION_SERVER)/emotion"
        self.RESTAURANT_LIST_URL = "\(RECOMMENDATION_SERVER)/recommendation/restaurant"
        self.SPORT_LIST_URL = "\(RECOMMENDATION_SERVER)/recommendation/sports"
        
        Core()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    var restaurantList: JSON = []
    var sportList: JSON = []
    
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
                    
                    Alamofire.request(
                        URL(string: self.RESTAURANT_LIST_URL)!,
                        method: .post,
                        parameters: json,
                        encoding: JSONEncoding.default).responseJSON { response in
                            // RESTAURANT list get!
                            print(response)
                            if let json = response.result.value{
                                // RESTAURANT list json get!
                                print("RESTAURANT json")
                                print(json)
                                self.restaurantList = JSON(json)
                                self.SetRestaurants()
                            }
                    }
                    Alamofire.request(
                        URL(string: self.SPORT_LIST_URL)!,
                        method: .post,
                        parameters: json,
                        encoding: JSONEncoding.default).responseJSON { response in
                            // SPORT list get!
                            print(response)
                            if let json = response.result.value{
                                // SPORT list json get!
                                print("SPORT json")
                                print(json)
                                self.sportList = JSON(json)
                                self.SetSports()
                            }
                    }
                }
        }
    }

    @IBOutlet weak var sportImageView: UIImageView!
    @IBOutlet weak var restaurantImageView: UIImageView!
    
    func SetRestaurants() {
//        let temp = self.restaurantList[0]
//        if let url = NSURL(string: temp["img"]) {
//            if let data = NSData(contentsOfURL: url) {
//                var image = UIImage(data: data)
//                
//            }        
//        }
        
        var imgListArray : [UIImage] = []
        for (_, restaurant) in self.restaurantList {
//            print ("new image!!!!!")
            let img = restaurant["img"]
            if let url = URL(string: "http://\(img)".addingPercentEncoding(withAllowedCharacters: .urlPathAllowed)!) {
                if let data = try? Data(contentsOf: url) {
//                    print ("add image!!!!!")
                    let newimage = UIImage(data: data)
                    imgListArray.append(newimage!)
                }
            }
        }
        
//        var imgListArray :NSMutableArray = []
//        for countValue in 1...11
//        {
//            
//            var strImageName : String = "c\(countValue).png"
//            var image  = UIImage(named:strImageName)
//            imgListArray .addObject(image)
//        }
        print ("image counts")
        print (imgListArray.count)
        self.restaurantImageView.animationImages = imgListArray
        self.restaurantImageView.animationDuration = 25.0
        self.restaurantImageView.startAnimating()
    }
    
    func SetSports() {
        
    }
}
