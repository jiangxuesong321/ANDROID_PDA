package com.android.pda.models;

import java.util.List;

public class ProductionStorage {

    public static class DBean {
        /**
         * MaterialDocumentYear : stri
         * MaterialDocument : string
         * InventoryTransactionType : st
         * DocumentDate : /Date(1492041600000)/
         * PostingDate : /Date(1492041600000)/
         * CreationDate : /Date(1492041600000)/
         * CreationTime : PT15H51M04S
         * CreatedByUser : string
         * MaterialDocumentHeaderText : string
         * ReferenceDocument : string
         * VersionForPrintingSlip : s
         * ManualPrintIsTriggered : s
         * CtrlPostgForExtWhseMgmtSyst : s
         * GoodsMovementCode : st
         * to_MaterialDocumentItem : {"results":[{"MaterialDocumentYear":"stri","MaterialDocument":"string","MaterialDocumentItem":"stri","Material":"string","Plant":"stri","StorageLocation":"stri","Batch":"string","GoodsMovementType":"str","InventoryStockType":"st","InventoryValuationType":"string","InventorySpecialStockType":"s","Supplier":"string","Customer":"string","SalesOrder":"string","SalesOrderItem":"string","SalesOrderScheduleLine":"stri","PurchaseOrder":"string","PurchaseOrderItem":"strin","WBSElement":"string","ManufacturingOrder":"string","ManufacturingOrderItem":"stri","GoodsMovementRefDocType":"s","GoodsMovementReasonCode":"stri","Delivery":"string","DeliveryItem":"string","AccountAssignmentCategory":"s","CostCenter":"string","ControllingArea":"stri","CostObject":"string","GLAccount":"string","FunctionalArea":"string","ProfitabilitySegment":"string","ProfitCenter":"string","MasterFixedAsset":"string","FixedAsset":"stri","MaterialBaseUnit":"str","QuantityInBaseUnit":"0","EntryUnit":"str","QuantityInEntryUnit":"0","CompanyCodeCurrency":"strin","GdsMvtExtAmtInCoCodeCrcy":"0","SlsPrcAmtInclVATInCoCodeCrcy":"0","FiscalYear":"stri","FiscalYearPeriod":"string","FiscalYearVariant":"st","IssgOrRcvgMaterial":"string","IssgOrRcvgBatch":"string","IssuingOrReceivingPlant":"stri","IssuingOrReceivingStorageLoc":"stri","IssuingOrReceivingStockType":"st","IssgOrRcvgSpclStockInd":"s","IssuingOrReceivingValType":"string","IsCompletelyDelivered":true,"MaterialDocumentItemText":"string","UnloadingPointName":"string","ShelfLifeExpirationDate":"/Date(1492041600000)/","ManufactureDate":"/Date(1492041600000)/","SerialNumbersAreCreatedAutomly":true,"Reservation":"string","ReservationItem":"stri","ReservationIsFinallyIssued":true,"SpecialStockIdfgSalesOrder":"string","SpecialStockIdfgSalesOrderItem":"string","SpecialStockIdfgWBSElement":"string","IsAutomaticallyCreated":"s","MaterialDocumentLine":"string","MaterialDocumentParentLine":"string","HierarchyNodeLevel":"st","GoodsMovementIsCancelled":true,"ReversedMaterialDocumentYear":"stri","ReversedMaterialDocument":"string","ReversedMaterialDocumentItem":"stri","ReferenceDocumentFiscalYear":"stri","InvtryMgmtRefDocumentItem":"stri","InvtryMgmtReferenceDocument":"string","MaterialDocumentPostingType":"s","InventoryUsabilityCode":"s","EWMWarehouse":"stri","EWMStorageBin":"string","DebitCreditCode":"s","to_MaterialDocumentHeader":"string","to_SerialNumbers":{"results":[{"Material":"string","SerialNumber":"string","MaterialDocument":"string","MaterialDocumentItem":"stri","MaterialDocumentYear":"stri"}]}}]}
         */

        private String MaterialDocumentYear;
        private String MaterialDocument;
        private String InventoryTransactionType;
        private String DocumentDate;
        private String PostingDate;
        private String CreationDate;
        private String CreationTime;
        private String CreatedByUser;
        private String MaterialDocumentHeaderText;
        private String ReferenceDocument;
        private String VersionForPrintingSlip;
        private String ManualPrintIsTriggered;
        private String CtrlPostgForExtWhseMgmtSyst;
        private String GoodsMovementCode;
        private ToMaterialDocumentItemBean to_MaterialDocumentItem;

        public String getMaterialDocumentYear() {
            return MaterialDocumentYear;
        }

        public void setMaterialDocumentYear(String MaterialDocumentYear) {
            this.MaterialDocumentYear = MaterialDocumentYear;
        }

        public String getMaterialDocument() {
            return MaterialDocument;
        }

        public void setMaterialDocument(String MaterialDocument) {
            this.MaterialDocument = MaterialDocument;
        }

        public String getInventoryTransactionType() {
            return InventoryTransactionType;
        }

        public void setInventoryTransactionType(String InventoryTransactionType) {
            this.InventoryTransactionType = InventoryTransactionType;
        }

        public String getDocumentDate() {
            return DocumentDate;
        }

        public void setDocumentDate(String DocumentDate) {
            this.DocumentDate = DocumentDate;
        }

        public String getPostingDate() {
            return PostingDate;
        }

        public void setPostingDate(String PostingDate) {
            this.PostingDate = PostingDate;
        }

        public String getCreationDate() {
            return CreationDate;
        }

        public void setCreationDate(String CreationDate) {
            this.CreationDate = CreationDate;
        }

        public String getCreationTime() {
            return CreationTime;
        }

        public void setCreationTime(String CreationTime) {
            this.CreationTime = CreationTime;
        }

        public String getCreatedByUser() {
            return CreatedByUser;
        }

        public void setCreatedByUser(String CreatedByUser) {
            this.CreatedByUser = CreatedByUser;
        }

        public String getMaterialDocumentHeaderText() {
            return MaterialDocumentHeaderText;
        }

        public void setMaterialDocumentHeaderText(String MaterialDocumentHeaderText) {
            this.MaterialDocumentHeaderText = MaterialDocumentHeaderText;
        }

        public String getReferenceDocument() {
            return ReferenceDocument;
        }

        public void setReferenceDocument(String ReferenceDocument) {
            this.ReferenceDocument = ReferenceDocument;
        }

        public String getVersionForPrintingSlip() {
            return VersionForPrintingSlip;
        }

        public void setVersionForPrintingSlip(String VersionForPrintingSlip) {
            this.VersionForPrintingSlip = VersionForPrintingSlip;
        }

        public String getManualPrintIsTriggered() {
            return ManualPrintIsTriggered;
        }

        public void setManualPrintIsTriggered(String ManualPrintIsTriggered) {
            this.ManualPrintIsTriggered = ManualPrintIsTriggered;
        }

        public String getCtrlPostgForExtWhseMgmtSyst() {
            return CtrlPostgForExtWhseMgmtSyst;
        }

        public void setCtrlPostgForExtWhseMgmtSyst(String CtrlPostgForExtWhseMgmtSyst) {
            this.CtrlPostgForExtWhseMgmtSyst = CtrlPostgForExtWhseMgmtSyst;
        }

        public String getGoodsMovementCode() {
            return GoodsMovementCode;
        }

        public void setGoodsMovementCode(String GoodsMovementCode) {
            this.GoodsMovementCode = GoodsMovementCode;
        }

        public ToMaterialDocumentItemBean getTo_MaterialDocumentItem() {
            return to_MaterialDocumentItem;
        }

        public void setTo_MaterialDocumentItem(ToMaterialDocumentItemBean to_MaterialDocumentItem) {
            this.to_MaterialDocumentItem = to_MaterialDocumentItem;
        }

        public static class ToMaterialDocumentItemBean {
            private List<ResultsBeanX> results;

            public List<ResultsBeanX> getResults() {
                return results;
            }

            public void setResults(List<ResultsBeanX> results) {
                this.results = results;
            }

            public static class ResultsBeanX {
                /**
                 * MaterialDocumentYear : stri
                 * MaterialDocument : string
                 * MaterialDocumentItem : stri
                 * Material : string
                 * Plant : stri
                 * StorageLocation : stri
                 * Batch : string
                 * GoodsMovementType : str
                 * InventoryStockType : st
                 * InventoryValuationType : string
                 * InventorySpecialStockType : s
                 * Supplier : string
                 * Customer : string
                 * SalesOrder : string
                 * SalesOrderItem : string
                 * SalesOrderScheduleLine : stri
                 * PurchaseOrder : string
                 * PurchaseOrderItem : strin
                 * WBSElement : string
                 * ManufacturingOrder : string
                 * ManufacturingOrderItem : stri
                 * GoodsMovementRefDocType : s
                 * GoodsMovementReasonCode : stri
                 * Delivery : string
                 * DeliveryItem : string
                 * AccountAssignmentCategory : s
                 * CostCenter : string
                 * ControllingArea : stri
                 * CostObject : string
                 * GLAccount : string
                 * FunctionalArea : string
                 * ProfitabilitySegment : string
                 * ProfitCenter : string
                 * MasterFixedAsset : string
                 * FixedAsset : stri
                 * MaterialBaseUnit : str
                 * QuantityInBaseUnit : 0
                 * EntryUnit : str
                 * QuantityInEntryUnit : 0
                 * CompanyCodeCurrency : strin
                 * GdsMvtExtAmtInCoCodeCrcy : 0
                 * SlsPrcAmtInclVATInCoCodeCrcy : 0
                 * FiscalYear : stri
                 * FiscalYearPeriod : string
                 * FiscalYearVariant : st
                 * IssgOrRcvgMaterial : string
                 * IssgOrRcvgBatch : string
                 * IssuingOrReceivingPlant : stri
                 * IssuingOrReceivingStorageLoc : stri
                 * IssuingOrReceivingStockType : st
                 * IssgOrRcvgSpclStockInd : s
                 * IssuingOrReceivingValType : string
                 * IsCompletelyDelivered : true
                 * MaterialDocumentItemText : string
                 * UnloadingPointName : string
                 * ShelfLifeExpirationDate : /Date(1492041600000)/
                 * ManufactureDate : /Date(1492041600000)/
                 * SerialNumbersAreCreatedAutomly : true
                 * Reservation : string
                 * ReservationItem : stri
                 * ReservationIsFinallyIssued : true
                 * SpecialStockIdfgSalesOrder : string
                 * SpecialStockIdfgSalesOrderItem : string
                 * SpecialStockIdfgWBSElement : string
                 * IsAutomaticallyCreated : s
                 * MaterialDocumentLine : string
                 * MaterialDocumentParentLine : string
                 * HierarchyNodeLevel : st
                 * GoodsMovementIsCancelled : true
                 * ReversedMaterialDocumentYear : stri
                 * ReversedMaterialDocument : string
                 * ReversedMaterialDocumentItem : stri
                 * ReferenceDocumentFiscalYear : stri
                 * InvtryMgmtRefDocumentItem : stri
                 * InvtryMgmtReferenceDocument : string
                 * MaterialDocumentPostingType : s
                 * InventoryUsabilityCode : s
                 * EWMWarehouse : stri
                 * EWMStorageBin : string
                 * DebitCreditCode : s
                 * to_MaterialDocumentHeader : string
                 * to_SerialNumbers : {"results":[{"Material":"string","SerialNumber":"string","MaterialDocument":"string","MaterialDocumentItem":"stri","MaterialDocumentYear":"stri"}]}
                 */

                private String MaterialDocumentYear;
                private String MaterialDocument;
                private String MaterialDocumentItem;
                private String Material;
                private String Plant;
                private String StorageLocation;
                private String Batch;
                private String GoodsMovementType;
                private String InventoryStockType;
                private String InventoryValuationType;
                private String InventorySpecialStockType;
                private String Supplier;
                private String Customer;
                private String SalesOrder;
                private String SalesOrderItem;
                private String SalesOrderScheduleLine;
                private String PurchaseOrder;
                private String PurchaseOrderItem;
                private String WBSElement;
                private String ManufacturingOrder;
                private String ManufacturingOrderItem;
                private String GoodsMovementRefDocType;
                private String GoodsMovementReasonCode;
                private String Delivery;
                private String DeliveryItem;
                private String AccountAssignmentCategory;
                private String CostCenter;
                private String ControllingArea;
                private String CostObject;
                private String GLAccount;
                private String FunctionalArea;
                private String ProfitabilitySegment;
                private String ProfitCenter;
                private String MasterFixedAsset;
                private String FixedAsset;
                private String MaterialBaseUnit;
                private String QuantityInBaseUnit;
                private String EntryUnit;
                private String QuantityInEntryUnit;
                private String CompanyCodeCurrency;
                private String GdsMvtExtAmtInCoCodeCrcy;
                private String SlsPrcAmtInclVATInCoCodeCrcy;
                private String FiscalYear;
                private String FiscalYearPeriod;
                private String FiscalYearVariant;
                private String IssgOrRcvgMaterial;
                private String IssgOrRcvgBatch;
                private String IssuingOrReceivingPlant;
                private String IssuingOrReceivingStorageLoc;
                private String IssuingOrReceivingStockType;
                private String IssgOrRcvgSpclStockInd;
                private String IssuingOrReceivingValType;
                private boolean IsCompletelyDelivered;
                private String MaterialDocumentItemText;
                private String UnloadingPointName;
                private String ShelfLifeExpirationDate;
                private String ManufactureDate;
                private boolean SerialNumbersAreCreatedAutomly;
                private String Reservation;
                private String ReservationItem;
                private boolean ReservationIsFinallyIssued;
                private String SpecialStockIdfgSalesOrder;
                private String SpecialStockIdfgSalesOrderItem;
                private String SpecialStockIdfgWBSElement;
                private String IsAutomaticallyCreated;
                private String MaterialDocumentLine;
                private String MaterialDocumentParentLine;
                private String HierarchyNodeLevel;
                private boolean GoodsMovementIsCancelled;
                private String ReversedMaterialDocumentYear;
                private String ReversedMaterialDocument;
                private String ReversedMaterialDocumentItem;
                private String ReferenceDocumentFiscalYear;
                private String InvtryMgmtRefDocumentItem;
                private String InvtryMgmtReferenceDocument;
                private String MaterialDocumentPostingType;
                private String InventoryUsabilityCode;
                private String EWMWarehouse;
                private String EWMStorageBin;
                private String DebitCreditCode;
                private String to_MaterialDocumentHeader;
                private ToSerialNumbersBean to_SerialNumbers;

                public String getMaterialDocumentYear() {
                    return MaterialDocumentYear;
                }

                public void setMaterialDocumentYear(String MaterialDocumentYear) {
                    this.MaterialDocumentYear = MaterialDocumentYear;
                }

                public String getMaterialDocument() {
                    return MaterialDocument;
                }

                public void setMaterialDocument(String MaterialDocument) {
                    this.MaterialDocument = MaterialDocument;
                }

                public String getMaterialDocumentItem() {
                    return MaterialDocumentItem;
                }

                public void setMaterialDocumentItem(String MaterialDocumentItem) {
                    this.MaterialDocumentItem = MaterialDocumentItem;
                }

                public String getMaterial() {
                    return Material;
                }

                public void setMaterial(String Material) {
                    this.Material = Material;
                }

                public String getPlant() {
                    return Plant;
                }

                public void setPlant(String Plant) {
                    this.Plant = Plant;
                }

                public String getStorageLocation() {
                    return StorageLocation;
                }

                public void setStorageLocation(String StorageLocation) {
                    this.StorageLocation = StorageLocation;
                }

                public String getBatch() {
                    return Batch;
                }

                public void setBatch(String Batch) {
                    this.Batch = Batch;
                }

                public String getGoodsMovementType() {
                    return GoodsMovementType;
                }

                public void setGoodsMovementType(String GoodsMovementType) {
                    this.GoodsMovementType = GoodsMovementType;
                }

                public String getInventoryStockType() {
                    return InventoryStockType;
                }

                public void setInventoryStockType(String InventoryStockType) {
                    this.InventoryStockType = InventoryStockType;
                }

                public String getInventoryValuationType() {
                    return InventoryValuationType;
                }

                public void setInventoryValuationType(String InventoryValuationType) {
                    this.InventoryValuationType = InventoryValuationType;
                }

                public String getInventorySpecialStockType() {
                    return InventorySpecialStockType;
                }

                public void setInventorySpecialStockType(String InventorySpecialStockType) {
                    this.InventorySpecialStockType = InventorySpecialStockType;
                }

                public String getSupplier() {
                    return Supplier;
                }

                public void setSupplier(String Supplier) {
                    this.Supplier = Supplier;
                }

                public String getCustomer() {
                    return Customer;
                }

                public void setCustomer(String Customer) {
                    this.Customer = Customer;
                }

                public String getSalesOrder() {
                    return SalesOrder;
                }

                public void setSalesOrder(String SalesOrder) {
                    this.SalesOrder = SalesOrder;
                }

                public String getSalesOrderItem() {
                    return SalesOrderItem;
                }

                public void setSalesOrderItem(String SalesOrderItem) {
                    this.SalesOrderItem = SalesOrderItem;
                }

                public String getSalesOrderScheduleLine() {
                    return SalesOrderScheduleLine;
                }

                public void setSalesOrderScheduleLine(String SalesOrderScheduleLine) {
                    this.SalesOrderScheduleLine = SalesOrderScheduleLine;
                }

                public String getPurchaseOrder() {
                    return PurchaseOrder;
                }

                public void setPurchaseOrder(String PurchaseOrder) {
                    this.PurchaseOrder = PurchaseOrder;
                }

                public String getPurchaseOrderItem() {
                    return PurchaseOrderItem;
                }

                public void setPurchaseOrderItem(String PurchaseOrderItem) {
                    this.PurchaseOrderItem = PurchaseOrderItem;
                }

                public String getWBSElement() {
                    return WBSElement;
                }

                public void setWBSElement(String WBSElement) {
                    this.WBSElement = WBSElement;
                }

                public String getManufacturingOrder() {
                    return ManufacturingOrder;
                }

                public void setManufacturingOrder(String ManufacturingOrder) {
                    this.ManufacturingOrder = ManufacturingOrder;
                }

                public String getManufacturingOrderItem() {
                    return ManufacturingOrderItem;
                }

                public void setManufacturingOrderItem(String ManufacturingOrderItem) {
                    this.ManufacturingOrderItem = ManufacturingOrderItem;
                }

                public String getGoodsMovementRefDocType() {
                    return GoodsMovementRefDocType;
                }

                public void setGoodsMovementRefDocType(String GoodsMovementRefDocType) {
                    this.GoodsMovementRefDocType = GoodsMovementRefDocType;
                }

                public String getGoodsMovementReasonCode() {
                    return GoodsMovementReasonCode;
                }

                public void setGoodsMovementReasonCode(String GoodsMovementReasonCode) {
                    this.GoodsMovementReasonCode = GoodsMovementReasonCode;
                }

                public String getDelivery() {
                    return Delivery;
                }

                public void setDelivery(String Delivery) {
                    this.Delivery = Delivery;
                }

                public String getDeliveryItem() {
                    return DeliveryItem;
                }

                public void setDeliveryItem(String DeliveryItem) {
                    this.DeliveryItem = DeliveryItem;
                }

                public String getAccountAssignmentCategory() {
                    return AccountAssignmentCategory;
                }

                public void setAccountAssignmentCategory(String AccountAssignmentCategory) {
                    this.AccountAssignmentCategory = AccountAssignmentCategory;
                }

                public String getCostCenter() {
                    return CostCenter;
                }

                public void setCostCenter(String CostCenter) {
                    this.CostCenter = CostCenter;
                }

                public String getControllingArea() {
                    return ControllingArea;
                }

                public void setControllingArea(String ControllingArea) {
                    this.ControllingArea = ControllingArea;
                }

                public String getCostObject() {
                    return CostObject;
                }

                public void setCostObject(String CostObject) {
                    this.CostObject = CostObject;
                }

                public String getGLAccount() {
                    return GLAccount;
                }

                public void setGLAccount(String GLAccount) {
                    this.GLAccount = GLAccount;
                }

                public String getFunctionalArea() {
                    return FunctionalArea;
                }

                public void setFunctionalArea(String FunctionalArea) {
                    this.FunctionalArea = FunctionalArea;
                }

                public String getProfitabilitySegment() {
                    return ProfitabilitySegment;
                }

                public void setProfitabilitySegment(String ProfitabilitySegment) {
                    this.ProfitabilitySegment = ProfitabilitySegment;
                }

                public String getProfitCenter() {
                    return ProfitCenter;
                }

                public void setProfitCenter(String ProfitCenter) {
                    this.ProfitCenter = ProfitCenter;
                }

                public String getMasterFixedAsset() {
                    return MasterFixedAsset;
                }

                public void setMasterFixedAsset(String MasterFixedAsset) {
                    this.MasterFixedAsset = MasterFixedAsset;
                }

                public String getFixedAsset() {
                    return FixedAsset;
                }

                public void setFixedAsset(String FixedAsset) {
                    this.FixedAsset = FixedAsset;
                }

                public String getMaterialBaseUnit() {
                    return MaterialBaseUnit;
                }

                public void setMaterialBaseUnit(String MaterialBaseUnit) {
                    this.MaterialBaseUnit = MaterialBaseUnit;
                }

                public String getQuantityInBaseUnit() {
                    return QuantityInBaseUnit;
                }

                public void setQuantityInBaseUnit(String QuantityInBaseUnit) {
                    this.QuantityInBaseUnit = QuantityInBaseUnit;
                }

                public String getEntryUnit() {
                    return EntryUnit;
                }

                public void setEntryUnit(String EntryUnit) {
                    this.EntryUnit = EntryUnit;
                }

                public String getQuantityInEntryUnit() {
                    return QuantityInEntryUnit;
                }

                public void setQuantityInEntryUnit(String QuantityInEntryUnit) {
                    this.QuantityInEntryUnit = QuantityInEntryUnit;
                }

                public String getCompanyCodeCurrency() {
                    return CompanyCodeCurrency;
                }

                public void setCompanyCodeCurrency(String CompanyCodeCurrency) {
                    this.CompanyCodeCurrency = CompanyCodeCurrency;
                }

                public String getGdsMvtExtAmtInCoCodeCrcy() {
                    return GdsMvtExtAmtInCoCodeCrcy;
                }

                public void setGdsMvtExtAmtInCoCodeCrcy(String GdsMvtExtAmtInCoCodeCrcy) {
                    this.GdsMvtExtAmtInCoCodeCrcy = GdsMvtExtAmtInCoCodeCrcy;
                }

                public String getSlsPrcAmtInclVATInCoCodeCrcy() {
                    return SlsPrcAmtInclVATInCoCodeCrcy;
                }

                public void setSlsPrcAmtInclVATInCoCodeCrcy(String SlsPrcAmtInclVATInCoCodeCrcy) {
                    this.SlsPrcAmtInclVATInCoCodeCrcy = SlsPrcAmtInclVATInCoCodeCrcy;
                }

                public String getFiscalYear() {
                    return FiscalYear;
                }

                public void setFiscalYear(String FiscalYear) {
                    this.FiscalYear = FiscalYear;
                }

                public String getFiscalYearPeriod() {
                    return FiscalYearPeriod;
                }

                public void setFiscalYearPeriod(String FiscalYearPeriod) {
                    this.FiscalYearPeriod = FiscalYearPeriod;
                }

                public String getFiscalYearVariant() {
                    return FiscalYearVariant;
                }

                public void setFiscalYearVariant(String FiscalYearVariant) {
                    this.FiscalYearVariant = FiscalYearVariant;
                }

                public String getIssgOrRcvgMaterial() {
                    return IssgOrRcvgMaterial;
                }

                public void setIssgOrRcvgMaterial(String IssgOrRcvgMaterial) {
                    this.IssgOrRcvgMaterial = IssgOrRcvgMaterial;
                }

                public String getIssgOrRcvgBatch() {
                    return IssgOrRcvgBatch;
                }

                public void setIssgOrRcvgBatch(String IssgOrRcvgBatch) {
                    this.IssgOrRcvgBatch = IssgOrRcvgBatch;
                }

                public String getIssuingOrReceivingPlant() {
                    return IssuingOrReceivingPlant;
                }

                public void setIssuingOrReceivingPlant(String IssuingOrReceivingPlant) {
                    this.IssuingOrReceivingPlant = IssuingOrReceivingPlant;
                }

                public String getIssuingOrReceivingStorageLoc() {
                    return IssuingOrReceivingStorageLoc;
                }

                public void setIssuingOrReceivingStorageLoc(String IssuingOrReceivingStorageLoc) {
                    this.IssuingOrReceivingStorageLoc = IssuingOrReceivingStorageLoc;
                }

                public String getIssuingOrReceivingStockType() {
                    return IssuingOrReceivingStockType;
                }

                public void setIssuingOrReceivingStockType(String IssuingOrReceivingStockType) {
                    this.IssuingOrReceivingStockType = IssuingOrReceivingStockType;
                }

                public String getIssgOrRcvgSpclStockInd() {
                    return IssgOrRcvgSpclStockInd;
                }

                public void setIssgOrRcvgSpclStockInd(String IssgOrRcvgSpclStockInd) {
                    this.IssgOrRcvgSpclStockInd = IssgOrRcvgSpclStockInd;
                }

                public String getIssuingOrReceivingValType() {
                    return IssuingOrReceivingValType;
                }

                public void setIssuingOrReceivingValType(String IssuingOrReceivingValType) {
                    this.IssuingOrReceivingValType = IssuingOrReceivingValType;
                }

                public boolean isIsCompletelyDelivered() {
                    return IsCompletelyDelivered;
                }

                public void setIsCompletelyDelivered(boolean IsCompletelyDelivered) {
                    this.IsCompletelyDelivered = IsCompletelyDelivered;
                }

                public String getMaterialDocumentItemText() {
                    return MaterialDocumentItemText;
                }

                public void setMaterialDocumentItemText(String MaterialDocumentItemText) {
                    this.MaterialDocumentItemText = MaterialDocumentItemText;
                }

                public String getUnloadingPointName() {
                    return UnloadingPointName;
                }

                public void setUnloadingPointName(String UnloadingPointName) {
                    this.UnloadingPointName = UnloadingPointName;
                }

                public String getShelfLifeExpirationDate() {
                    return ShelfLifeExpirationDate;
                }

                public void setShelfLifeExpirationDate(String ShelfLifeExpirationDate) {
                    this.ShelfLifeExpirationDate = ShelfLifeExpirationDate;
                }

                public String getManufactureDate() {
                    return ManufactureDate;
                }

                public void setManufactureDate(String ManufactureDate) {
                    this.ManufactureDate = ManufactureDate;
                }

                public boolean isSerialNumbersAreCreatedAutomly() {
                    return SerialNumbersAreCreatedAutomly;
                }

                public void setSerialNumbersAreCreatedAutomly(boolean SerialNumbersAreCreatedAutomly) {
                    this.SerialNumbersAreCreatedAutomly = SerialNumbersAreCreatedAutomly;
                }

                public String getReservation() {
                    return Reservation;
                }

                public void setReservation(String Reservation) {
                    this.Reservation = Reservation;
                }

                public String getReservationItem() {
                    return ReservationItem;
                }

                public void setReservationItem(String ReservationItem) {
                    this.ReservationItem = ReservationItem;
                }

                public boolean isReservationIsFinallyIssued() {
                    return ReservationIsFinallyIssued;
                }

                public void setReservationIsFinallyIssued(boolean ReservationIsFinallyIssued) {
                    this.ReservationIsFinallyIssued = ReservationIsFinallyIssued;
                }

                public String getSpecialStockIdfgSalesOrder() {
                    return SpecialStockIdfgSalesOrder;
                }

                public void setSpecialStockIdfgSalesOrder(String SpecialStockIdfgSalesOrder) {
                    this.SpecialStockIdfgSalesOrder = SpecialStockIdfgSalesOrder;
                }

                public String getSpecialStockIdfgSalesOrderItem() {
                    return SpecialStockIdfgSalesOrderItem;
                }

                public void setSpecialStockIdfgSalesOrderItem(String SpecialStockIdfgSalesOrderItem) {
                    this.SpecialStockIdfgSalesOrderItem = SpecialStockIdfgSalesOrderItem;
                }

                public String getSpecialStockIdfgWBSElement() {
                    return SpecialStockIdfgWBSElement;
                }

                public void setSpecialStockIdfgWBSElement(String SpecialStockIdfgWBSElement) {
                    this.SpecialStockIdfgWBSElement = SpecialStockIdfgWBSElement;
                }

                public String getIsAutomaticallyCreated() {
                    return IsAutomaticallyCreated;
                }

                public void setIsAutomaticallyCreated(String IsAutomaticallyCreated) {
                    this.IsAutomaticallyCreated = IsAutomaticallyCreated;
                }

                public String getMaterialDocumentLine() {
                    return MaterialDocumentLine;
                }

                public void setMaterialDocumentLine(String MaterialDocumentLine) {
                    this.MaterialDocumentLine = MaterialDocumentLine;
                }

                public String getMaterialDocumentParentLine() {
                    return MaterialDocumentParentLine;
                }

                public void setMaterialDocumentParentLine(String MaterialDocumentParentLine) {
                    this.MaterialDocumentParentLine = MaterialDocumentParentLine;
                }

                public String getHierarchyNodeLevel() {
                    return HierarchyNodeLevel;
                }

                public void setHierarchyNodeLevel(String HierarchyNodeLevel) {
                    this.HierarchyNodeLevel = HierarchyNodeLevel;
                }

                public boolean isGoodsMovementIsCancelled() {
                    return GoodsMovementIsCancelled;
                }

                public void setGoodsMovementIsCancelled(boolean GoodsMovementIsCancelled) {
                    this.GoodsMovementIsCancelled = GoodsMovementIsCancelled;
                }

                public String getReversedMaterialDocumentYear() {
                    return ReversedMaterialDocumentYear;
                }

                public void setReversedMaterialDocumentYear(String ReversedMaterialDocumentYear) {
                    this.ReversedMaterialDocumentYear = ReversedMaterialDocumentYear;
                }

                public String getReversedMaterialDocument() {
                    return ReversedMaterialDocument;
                }

                public void setReversedMaterialDocument(String ReversedMaterialDocument) {
                    this.ReversedMaterialDocument = ReversedMaterialDocument;
                }

                public String getReversedMaterialDocumentItem() {
                    return ReversedMaterialDocumentItem;
                }

                public void setReversedMaterialDocumentItem(String ReversedMaterialDocumentItem) {
                    this.ReversedMaterialDocumentItem = ReversedMaterialDocumentItem;
                }

                public String getReferenceDocumentFiscalYear() {
                    return ReferenceDocumentFiscalYear;
                }

                public void setReferenceDocumentFiscalYear(String ReferenceDocumentFiscalYear) {
                    this.ReferenceDocumentFiscalYear = ReferenceDocumentFiscalYear;
                }

                public String getInvtryMgmtRefDocumentItem() {
                    return InvtryMgmtRefDocumentItem;
                }

                public void setInvtryMgmtRefDocumentItem(String InvtryMgmtRefDocumentItem) {
                    this.InvtryMgmtRefDocumentItem = InvtryMgmtRefDocumentItem;
                }

                public String getInvtryMgmtReferenceDocument() {
                    return InvtryMgmtReferenceDocument;
                }

                public void setInvtryMgmtReferenceDocument(String InvtryMgmtReferenceDocument) {
                    this.InvtryMgmtReferenceDocument = InvtryMgmtReferenceDocument;
                }

                public String getMaterialDocumentPostingType() {
                    return MaterialDocumentPostingType;
                }

                public void setMaterialDocumentPostingType(String MaterialDocumentPostingType) {
                    this.MaterialDocumentPostingType = MaterialDocumentPostingType;
                }

                public String getInventoryUsabilityCode() {
                    return InventoryUsabilityCode;
                }

                public void setInventoryUsabilityCode(String InventoryUsabilityCode) {
                    this.InventoryUsabilityCode = InventoryUsabilityCode;
                }

                public String getEWMWarehouse() {
                    return EWMWarehouse;
                }

                public void setEWMWarehouse(String EWMWarehouse) {
                    this.EWMWarehouse = EWMWarehouse;
                }

                public String getEWMStorageBin() {
                    return EWMStorageBin;
                }

                public void setEWMStorageBin(String EWMStorageBin) {
                    this.EWMStorageBin = EWMStorageBin;
                }

                public String getDebitCreditCode() {
                    return DebitCreditCode;
                }

                public void setDebitCreditCode(String DebitCreditCode) {
                    this.DebitCreditCode = DebitCreditCode;
                }

                public String getTo_MaterialDocumentHeader() {
                    return to_MaterialDocumentHeader;
                }

                public void setTo_MaterialDocumentHeader(String to_MaterialDocumentHeader) {
                    this.to_MaterialDocumentHeader = to_MaterialDocumentHeader;
                }

                public ToSerialNumbersBean getTo_SerialNumbers() {
                    return to_SerialNumbers;
                }

                public void setTo_SerialNumbers(ToSerialNumbersBean to_SerialNumbers) {
                    this.to_SerialNumbers = to_SerialNumbers;
                }

                public static class ToSerialNumbersBean {
                    private List<ResultsBean> results;

                    public List<ResultsBean> getResults() {
                        return results;
                    }

                    public void setResults(List<ResultsBean> results) {
                        this.results = results;
                    }

                    public static class ResultsBean {
                        /**
                         * Material : string
                         * SerialNumber : string
                         * MaterialDocument : string
                         * MaterialDocumentItem : stri
                         * MaterialDocumentYear : stri
                         */

                        private String Material;
                        private String SerialNumber;
                        private String MaterialDocument;
                        private String MaterialDocumentItem;
                        private String MaterialDocumentYear;

                        public String getMaterial() {
                            return Material;
                        }

                        public void setMaterial(String Material) {
                            this.Material = Material;
                        }

                        public String getSerialNumber() {
                            return SerialNumber;
                        }

                        public void setSerialNumber(String SerialNumber) {
                            this.SerialNumber = SerialNumber;
                        }

                        public String getMaterialDocument() {
                            return MaterialDocument;
                        }

                        public void setMaterialDocument(String MaterialDocument) {
                            this.MaterialDocument = MaterialDocument;
                        }

                        public String getMaterialDocumentItem() {
                            return MaterialDocumentItem;
                        }

                        public void setMaterialDocumentItem(String MaterialDocumentItem) {
                            this.MaterialDocumentItem = MaterialDocumentItem;
                        }

                        public String getMaterialDocumentYear() {
                            return MaterialDocumentYear;
                        }

                        public void setMaterialDocumentYear(String MaterialDocumentYear) {
                            this.MaterialDocumentYear = MaterialDocumentYear;
                        }
                    }
                }
            }
        }
    }
}
