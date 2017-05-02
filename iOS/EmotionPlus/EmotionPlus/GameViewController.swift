//
//  SecondViewController.swift
//  EmotionPlus
//
//  Created by Xiaozhe Wang on 4/28/17.
//  Copyright © 2017 iot17steam5. All rights reserved.
//

import UIKit
import Foundation

class GameViewController: UIViewController, UINavigationControllerDelegate, UIImagePickerControllerDelegate {

    @IBOutlet var imageView: UIImageView!
    
    var imagePicker: UIImagePickerController!
    
    @IBAction func takePhoto(_ sender: Any) {
        imagePicker =  UIImagePickerController()
        imagePicker.delegate = self
        imagePicker.sourceType = .camera
        
        present(imagePicker, animated: true, completion: nil)
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        let headers = [
            "content-type": "application/octet-stream"
        ]
        
        let postData = NSData.init(data: UIImageJPEGRepresentation(imageView.image!, 0.7)!)
        
        //        NSData(data: "dddd".data(using: String.Encoding.utf8)!)
        
        let request = NSMutableURLRequest(url: NSURL(string: "http://172.29.93.218:3000/analyze")! as URL,
                                          cachePolicy: .useProtocolCachePolicy,
                                          timeoutInterval: 10.0)
        request.httpMethod = "POST"
        request.allHTTPHeaderFields = headers
        request.httpBody = postData as Data
        
        let session = URLSession.shared
        let dataTask = session.dataTask(with: request as URLRequest, completionHandler: { (data, response, error) -> Void in
            if (error != nil) {
                print(error)
            } else {
                let httpResponse = response as? HTTPURLResponse
                print(httpResponse)
                if httpResponse?.statusCode == 200{
                    let json = try! JSONSerialization.jsonObject(with: data!, options: []) as! [String : Any]
                    print(json)
                    self.originFace.append(json["anger"] as! Float)
                    self.originFace.append(json["fear"] as! Float)
                    self.originFace.append(json["surprise"] as! Float)
                    self.originFace.append(json["contempt"] as! Float)
                    self.originFace.append(json["disgust"] as! Float)
                    self.originFace.append(json["happiness"] as! Float)
                    self.originFace.append(json["neutral"] as! Float)
                    self.originFace.append(json["sadness"] as! Float)
                }
            }
        })
        
        dataTask.resume()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        imagePicker.dismiss(animated: true, completion: nil)
        imageView.image = info[UIImagePickerControllerOriginalImage] as? UIImage
        
        let headers = [
            "content-type": "application/octet-stream"
        ]
        
        let postData = NSData.init(data: UIImageJPEGRepresentation(imageView.image!, 0.7)!)
        
//        NSData(data: "dddd".data(using: String.Encoding.utf8)!)
        
        let request = NSMutableURLRequest(url: NSURL(string: "http://172.29.93.218:3000/analyze")! as URL,
                                          cachePolicy: .useProtocolCachePolicy,
                                          timeoutInterval: 10.0)
        request.httpMethod = "POST"
        request.allHTTPHeaderFields = headers
        request.httpBody = postData as Data
        
        let session = URLSession.shared
        let dataTask = session.dataTask(with: request as URLRequest, completionHandler: { (data, response, error) -> Void in
            if (error != nil) {
                print(error)
            } else {
                let httpResponse = response as? HTTPURLResponse
                print(httpResponse)
                if httpResponse?.statusCode == 200{
                    let json = try! JSONSerialization.jsonObject(with: data!, options: []) as! [String : Any]
                    print(json)
                    self.yourFace.append(json["anger"] as! Float)
                    self.yourFace.append(json["fear"] as! Float)
                    self.yourFace.append(json["surprise"] as! Float)
                    self.yourFace.append(json["contempt"] as! Float)
                    self.yourFace.append(json["disgust"] as! Float)
                    self.yourFace.append(json["happiness"] as! Float)
                    self.yourFace.append(json["neutral"] as! Float)
                    self.yourFace.append(json["sadness"] as! Float)
                    var sum : Float = 0.0
                    for i in 0..<self.originFace.count {
                        sum += abs(self.originFace[i] - self.yourFace[i])
                    }
                    let s : Float = round((2.0 - sum) / 2.0 * 100)
                    self.score.text = String(s)
                } else {
                    self.score.text = "0"
                }
            }
        })
        
        dataTask.resume()
    }
    
    var originFace : [Float] = []
    var yourFace : [Float] = []
    
    @IBOutlet var score: UILabel!
}

