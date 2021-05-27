(ns hello-seesaw.core
  (:import org.pushingpixels.substance.api.SubstanceLookAndFeel))

(use 'seesaw.core)

(defn handler [event]
  (let [e (.getActionCommand event)]
    (if (= e "Close Frame")
      (System/exit 0))
    (if (= e "Theme Select"
           )
      (-> (frame :title "Themes" :id 3
                 :content (theme-select)
                 :on-close :hide :height 400 :width 600)))))


(def close-frame (menu-item :text "Close Frame"
                           :tip "This will close the frame"
                           :listen [:action handler]))

(def theme-select (menu-item :text "Theme Select"
                             :tip "This will allow you to select a theme"
                             :listen [:action handler]))

(defn theme-selector []
  (horizontal-panel
   :items [
           (combobox
            :model (vals (SubstanceLookAndFeel/getAllSkins))
            :renderer (fn [this {:keys [value]}]
                        (text! this (.getClassName value)))
            :listen [:selection (fn [e]
                                  (invoke-later
                                   (-> e
                                       selection
                                       .getClassName
                                       SubstanceLookAndFeel/setSkin)))])
           ]))
(def jframe (frame :title "hello Frame"
                   :menubar (menubar :items [(menu :text "File" :items [close-frame])])
                   :height 400
                   :width 600
                   :on-close :exit
                   :content (label :text "Hello this is a label!")
                   ))

(defn -main []
  (native!)
  (invoke-later
   (-> jframe pack! show!)
   (SubstanceLookAndFeel/setSkin "org.pushingpixels.substance.api.skin.GraphiteAquaSkin")))
