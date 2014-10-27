(ns edeposit.amqp.epubcheck-test
  (:require [clojure.java.io :as io]
            [clojure.data.xml :as x]
            [clojure.data.zip.xml :as xml]
            [edeposit.amqp.epubcheck.core :as core]
            [clojure.pprint :as pp]
            )
  (:use clojure.test))

(deftest epub-file-test-01
  (let [fname "resources/vPrompt-Sample-EPUB2.epub"
        result (core/validate fname)
        ]
    (testing "check valid epub2 file and its metadata"
      (is (= (:isWellFormedEPUB2 result) true))
      (is (= (:isWellFormedEPUB3 result) false))
      (is (.startsWith (:xml result) "<?xml version"))
      (is (.contains (:xml result) "<jhove"))
      (is (= (:validationMessages result) ()))
      )
    )
  )

(deftest epub-file-test-02
  (let [fname "resources/vPrompt-Sample-EPUB3.epub"
        result (core/validate fname)
        ]
    (testing "check valid epub2 file and its metadata"
      (is (= (:isWellFormedEPUB2 result) false))
      (is (= (:isWellFormedEPUB3 result) true))
      (is (.startsWith (:xml result) "<?xml version"))
      (is (.contains (:xml result) "<jhove"))
      (is (not (=  (:validationMessages result) ())))
      )
    )
  )

(deftest epub-file-test-03
  (let [fname "resources/invalid/mimetypeAndVersion.epub"
        result (core/validate fname)
        messages (list "OPF-019, FATAL, [Spine tag was not found in the OPF file.], OEBPS/content.opf"
                       "PKG-006, ERROR, [Mimetype file entry is missing or is not the first file in the archive.], mimetypeAndVersion.epub"
                       "OPF-024, ERROR, [Found unknown ePub version 0.0.], mimetypeAndVersion.epub"
                       "OPF-001, ERROR, [There was an error when parsing the ePub version: Version attribute not found.], OEBPS/content.opf")
        ]
    (testing "check invalid epub2 file and validation messages"
      (is (= (:isWellFormedEPUB2 result) false))
      (is (= (:isWellFormedEPUB3 result) false))
      (is (.startsWith (:xml result) "<?xml version"))
      (is (.contains (:xml result) "<jhove"))
      (is (= (:validationMessages result) messages))
      )
    )
  )