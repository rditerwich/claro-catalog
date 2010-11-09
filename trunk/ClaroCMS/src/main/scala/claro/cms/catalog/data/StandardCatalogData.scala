package claro.cms.catalog.data

import claro.cms.{Cms,Dao}
import claro.cms.catalog.{CatalogDao}
import claro.jpa.catalog._
import claro.jpa.shop._
import claro.common.util.Conversions._
import scala.collection.JavaConversions._

object StandardCatalogData extends Dao {

  val dataSource = CatalogDao.dataSource
  val Properties = CatalogDao.Properties
  
  val supplies = CatalogDao.createCategory("Supplies", "Tetterode levert een uitgebreid assortiment aan verbruiksmaterialen.", getClass, "saphira_banner.jpg", CatalogDao.catalog.getRoot)
  val spareParts = CatalogDao.createCategory("Spare Parts", "Tetterode levert een uitgebreid assortiment aan onderdelen.", getClass, "", CatalogDao.catalog.getRoot)
  val services = CatalogDao.createCategory("Services", "Tetterode levert een uitgebreid assortiment aan services.", getClass, "", CatalogDao.catalog.getRoot)
  
  val prePress = CatalogDao.createCategory("Pre Press", "", getClass, "prepress.jpg", CatalogDao.catalog.getRoot)
  val press = CatalogDao.createCategory("Press", "", getClass, "", CatalogDao.catalog.getRoot)
  val postPress = CatalogDao.createCategory("Post Press", "", getClass, "", CatalogDao.catalog.getRoot)
  
  def machines = CatalogDao.getOrCreateCategory("Machines")
  
  def cutters = CatalogDao.getOrCreateCategory("Cutters")
  
  def shop = CatalogDao.getOrCreateShop("Shop")

  def brands = CatalogDao.getOrCreateCategory("Brands")
  def abdick = CatalogDao.getOrCreateCategory("Abdick")
  def agfa = CatalogDao.getOrCreateCategory("Agfa")
  def presstek = CatalogDao.getOrCreateCategory("Presstek")
  def flintgroup = CatalogDao.getOrCreateCategory("The Flint Group")
  def saphira = CatalogDao.getOrCreateCategory("Saphira")
  def hp = CatalogDao.getOrCreateCategory("Hewlett Packard")
  def epson = CatalogDao.getOrCreateCategory("Epson")

  def ink = CatalogDao.getOrCreateCategory("Ink")
  def blackInk = CatalogDao.getOrCreateCategory("Black Ink")
  def colorInk = CatalogDao.getOrCreateCategory("Color Ink")

  val SM74 = CatalogDao.createCategory("Speedmaster SM 74", "De Speedmaster SM 74 heeft zich sinds de introductie in 1993 ontwikkeld als de belangrijkste persautomaat in het middenformaat. Als geen ander beschikt de SM 74 over de Speedmaster automatiseringskenmerken, gecombineerd met flexibiliteit en een breed aanbod in verschillende modellen en uitvoeringen. Van 1 tot 10 kleuren, met of zonder lakwerk, voor alle bedrijfsgrootten en voor de meest uiteenlopende drukorders is de SM 74 beschikbaar. In de Speedmaster SM 74 serie heeft Heidelberg het succesvolle \"One Pass Productivity\"-concept van schoon- en weerdruk in een doorgang met de beproefde keertrommel met tangengrijper en verwisselbare tegendrukcilinders op de drukwerken na het keren van het vel, toegepast.", getClass, "speedmastersm74.jpg", machines)
  val polar66 = CatalogDao.getOrCreateProduct("Polar 66 Quickcutter") useIn { product => 
    CatalogDao.set(product, Properties.articleNumber, "MA18364876")
    CatalogDao.set(product, Properties.description, "Polar snijmachine, model 66", "nl")
    CatalogDao.set(product, Properties.synopsis, "De Polar 66 is een echte 'dienstverlener' in de copy-shop branche, huisdrukkerij en ieder die in de steeds groeiende franchisemarkt een betrouwbare snijmachine zoekt. <p>Meer informatie over de Polar 66? Bel met Tetterode, Sales Support Finishing, tel. 020 44 66 999 of vul het contactformulier in, dan nemen wij contact met u op.", "nl")
    CatalogDao.set(product, Properties.price, 18000.00)
    CatalogDao.setImage(product, Properties.image, getClass, "polar66.png")
    cutters.getChildren.add(product)
  }
  val epsonStylusPro4880 = CatalogDao.createCategory("Epson Stylus Pro 4880", "Ontdek de hoogste kwaliteit printen met Epson 8-kleuren UltraChrome K3 inkt met Vivid Magenta-technologie. Breng fotografie, fine art en proefdrukken tot leven met perfecte reproductie, stabiliteit en consistentie van afbeeldingen. Produceer het hoogste niveau in printkwaliteit en betrouwbaarheid met de laatste snufjes van Epson in inkttechnologieen en verbeterde verwerking van afbeeldingen. Creeer keer op keer blijvende fotorealistische afbeeldingen met UltraChrome K3-inkt van Epson met Vivid Magenta-technologie en drie zwarte inkten. Stel nieuwe standaarden op een serie van media met verbeterde gradaties in kleur, vrijwel zonder metamerisme en een minimale glansdifferentiaal. Verbeter daarbij uw productiviteit met hoge snelheden, nieuwe stuurprogramma's en eenvoudige controle.", getClass, "stylus_pro_4880.jpg", machines)

  def createNavigation(shop : Shop, parent : Navigation, category : Category, index : Int) = 
    new Navigation useIn { nav =>
      nav.setCategory(category)
      nav.setIndex(index)
      if (shop != null) {
        nav.setParentShop(shop)
        shop.getNavigation.add(nav)
      }
      if (parent != null) {
        nav.setParentNavigation(parent)
        parent.getSubNavigation.add(nav)
      }
    }
  
  def createSampleData() = transaction { em =>
    CatalogDao.catalog.setName("Catalog")
    val root = CatalogDao.catalog.getRoot
    
    root.getChildren.clear
    root.getChildren.add(machines)
    root.getChildren.add(brands)
    
    machines.getChildren.add(cutters)

    brands.getChildren.add(abdick)
    brands.getChildren.add(agfa)
    brands.getChildren.add(flintgroup)
    brands.getChildren.add(presstek)
    brands.getChildren.add(saphira)
    brands.getChildren.add(hp)
    brands.getChildren.add(epson)
    
    adhesiveBinding
    abdickSupplies
    plaat
    inks
    hpproducts
    epsonproducts
    
    shop.getNavigation.clear()
    createNavigation(shop, null, null, 0) useIn { nav =>
      createNavigation(null, nav, machines, 0)
      createNavigation(null, nav, brands, 1)
    }
    createNavigation(shop, null, null, 1) useIn { nav =>
      createNavigation(null, nav, supplies, 0)
      createNavigation(null, nav, spareParts, 1)
      createNavigation(null, nav, services, 2)
    }
    createNavigation(shop, null, null, 2) useIn { nav =>
      createNavigation(null, nav, prePress, 0)
      createNavigation(null, nav, press, 1)
      createNavigation(null, nav, postPress, 2)
    }
    
    new VolumeDiscountPromotion useIn { promotion =>
      promotion.setProduct(polar66)
      promotion.setVolumeDiscount(1)
      promotion.setPrice(14990.00)
      promotion.setStartDate(new java.util.Date())
      promotion.setEndDate(new java.util.Date(System.currentTimeMillis + 40 * 24 * 60 * 60 * 1000))
      promotion.setShop(shop)
      shop.getPromotions.add(promotion)
    }
  }
  
  def adhesiveBinding = {
    val adhesiveBinding = CatalogDao.getOrCreateCategory("Adhesive Binding")
    postPress.getChildren.add(adhesiveBinding)
  }
  
  def abdickSupplies = {
//  	val abdickSupplies = CatalogDao.getOrCreateCategory("Abdick Supplies")
//  	supplies.getChildren.add(abdickSupplies)
//  	press.getChildren.add(abdickSupplies)
  	val p1 = CatalogDao.createProduct("AB Dick Spacer", "", "P-36302", "", 9, getClass, "abdick-spacer.jpg", List(press, spareParts, abdick))
  	val p2 = CatalogDao.createProduct("AB Dick Pressure", "", "P-36793", "", 114.55, getClass, "abdick-pressure.jpg", List(press, spareParts, abdick))
  	val p3 = CatalogDao.createProduct("AB Dick Shield", "", "P-36210", "", 77.62, getClass, "abdick-shield.jpg", List(press, spareParts, abdick))
  	CatalogDao.setRelated("supplies", SM74, p1, p2)
  }
  
  def plaat = {
    val plates = CatalogDao.getOrCreateCategory("Plates")
    prePress.getChildren.add(plates)
    supplies.getChildren.add(plates)
    CatalogDao.createProduct("Presstek Anthem Pro", "", "SU18551200", "Presstek has unveiled the Presstek Anthem Pro chemistry-free thermally imaged digital plate. It requires only a water rinse after imaging to prepare it for printing. It does not require gumming, baking, or chemical processing and supports run lengths up to 100,000 impressions. It is compatible with Presstek's Dimension thermal platesetters.", 18.15, getClass, "presstekanthempro.png", List(presstek, plates))
    CatalogDao.createProduct("NYLOFLEX SPRINT DIGITAL FLEXO PLATE", "", "SU18551341", "Flint Group Flexographic Products has launched a digital version of its water washable flexo printing plate nyloflex Sprint. Designed to meet the high quality requirements of the narrow web and the mid web market, nyloflex Sprint Digital offers high resistance against UV-inks and UV-varnishes and a remarkable performance in printing of finest elements, up to 60 L/cm or even higher. Due to the excellent ink transfer characteristics and low dot gain the nyloflex Sprint Digital shows an outstanding performance in halftone printing.", 22.69, getClass, "nyloflex.png", List(flintgroup, plates))
    CatalogDao.createProduct("Anthem Pro", "6Mil Boxed 9-3/8 x 13-3/8", "PCG08831", "", 201.15, getClass, "anthempro.jpg", List(presstek, plates))
    CatalogDao.createProduct("Anthem Pro", "6Mil Boxed 9-1/2 x 13-3/8", "PCG13511", "", 203.83, getClass, "anthempro.jpg", List(presstek, plates))
    CatalogDao.createProduct("Anthem Pro", "6Mil Boxed 10 x 15", "PCG05911", "", 240.63, getClass, "anthempro.jpg", List(presstek, plates))
    CatalogDao.createProduct("Anthem Pro", "6Mil Boxed 10 x 15-1/2", "PCG08051", "", 248.65, getClass, "anthempro.jpg", List(presstek, plates))
    CatalogDao.createProduct("Anthem Pro", "6Mil Boxed 10 x 15-9/32", "PCG06091", "", 245.14, getClass, "anthempro.jpg", List(presstek, plates))
    CatalogDao.createProduct("Anthem Pro", "6Mil Boxed 10 x 16", "PCG07061", "", 256.67, getClass, "anthempro.jpg", List(presstek, plates))
    CatalogDao.createProduct("Anthem Pro", "6Mil Pallet 9-3/8 x 13-3/8", "PCG08831", "", 1910.15, getClass, "anthempro.jpg", List(presstek, plates))
    CatalogDao.createProduct("Anthem Pro", "6Mil Pallet 9-1/2 x 13-3/8", "PCG13511", "", 1930.83, getClass, "anthempro.jpg", List(presstek, plates))
    CatalogDao.createProduct("Anthem Pro", "6Mil Pallet 10 x 15", "PCG05911", "", 2100.63, getClass, "anthempro.jpg", List(presstek, plates))
    CatalogDao.createProduct("Anthem Pro", "6Mil Pallet 10 x 15-1/2", "PCG08051", "", 2180.65, getClass, "anthempro.jpg", List(presstek, plates))
    CatalogDao.createProduct("Anthem Pro", "6Mil Pallet 10 x 15-9/32", "PCG06091", "", 2150.14, getClass, "anthempro.jpg", List(presstek, plates))
    CatalogDao.createProduct("Anthem Pro", "6Mil Pallet 10 x 16", "PCG07061", "", 2260.67, getClass, "anthempro.jpg", List(presstek, plates))
    
    CatalogDao.createProduct("AGFA Lithostar Ultra", "90-25-19/32X21-21/32 LAPV+ 8MIL", "PCG07061", "90-25-19/32X21-21/32 LAPV+ 8MIL", 646.65, getClass, "agfalithostar.jpg", List(agfa, plates))
    CatalogDao.createProduct("AGFA Lithostar Chemistry", "20L-AGFA L5000B ULTRA DEVELOPER", "LXAZW000", "20L-AGFA L5000B ULTRA DEVELOPER", 131.55, getClass, "agfalithostar.jpg", List(agfa, plates))
    CatalogDao.createProduct("AGFA Lithostar Chemistry", "20L-AGFA L5000B ULTRA DEVELOPER", "OETTT000", "20L-AGFA L5000B ULTRA DEVELOPER", 120.20, getClass, "agfalithostar.jpg", List(agfa, plates))
    CatalogDao.createProduct("AGFA Lithostar Chemistry", "3-LITHOSTAR LAPV CORR PEN BROAD TIP", "P9EHY000", "3-LITHOSTAR LAPV CORR PEN BROAD TIP", 45.05, getClass, "agfalithostar.jpg", List(agfa, plates))
  }
  
  def inks = {
	  press.getChildren.add(ink)
	  supplies.getChildren.add(ink)
	  val processInkSet = CatalogDao.getOrCreateCategory("Process Ink Sets")
	  val magneticInks = CatalogDao.getOrCreateCategory("Magnetic Inks")
	  ink.getChildren.add(blackInk)
	  ink.getChildren.add(colorInk)
	  ink.getChildren.add(processInkSet)
	  ink.getChildren.add(magneticInks)
	  CatalogDao.createProduct("Abdick RB900 black CAN", "Abdick RB900 black rubber base CAN", "83-9-104411", "", 9.97, getClass, "abdick-can.jpg", List(abdick, blackInk))
	  CatalogDao.createProduct("Abdick RB900 black 5LB", "Abdick RB900 black rubber base 5LB CAN", "83-9-104411", "", 48.11, getClass, "abdick-5lb.jpg", List(abdick, blackInk))
	  CatalogDao.createProduct("Abdick RB900 black CRT", "Abdick RB900 black rubber base Cartiridge", "83-9-104411", "", 9.39, getClass, "abdick-crt.jpg", List(abdick, blackInk))
	 
	  val saphiraInks = CatalogDao.getOrCreateCategory("Saphira Inks")
	  saphira.getChildren.add(saphiraInks)
	  ink.getChildren.add(saphiraInks)
	  val saphiraBioMag = CatalogDao.createProduct("Saphira Ink Bio-speed Magenta", "", "SU18364577", "Magenta bio process ink for best results at a high speed", 8.55, getClass, "saphiraBioMag.jpg", List(saphiraInks, colorInk))
	  CatalogDao.createProduct("Saphira Ink Bio-speed Yellow", "SU18364578", "", "Yellow bio process ink for best results at a high speed", 8.55, getClass, "saphiraBioYellow.jpg", List(saphiraInks, colorInk))
	  CatalogDao.createProduct("Saphira Ink Bio-speed Blue", "SU18364579", "", "Blue bio process ink for best results at a high speed", 8.55, getClass, "saphiraBioBlue.jpg", List(saphiraInks, colorInk))
	
  }
  
  def epsonproducts = {
		val epsonInks = CatalogDao.getOrCreateCategory("Epson Inks")
		ink.getChildren.add(epsonInks)
		epson.getChildren.add(epsonInks)
		CatalogDao.createProduct("Epson 11880", "700 ml Cyan", "3067321", "", 151.80, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T591200")
		CatalogDao.createProduct("Epson 11880", "700 ml Light Cyan", "3067324", "", 151.80, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T591500")
		CatalogDao.createProduct("Epson 11880", "700 ml Light Light Black", "3067328", "", 151.80, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T591900")
		CatalogDao.createProduct("Epson 11880", "700 ml Matt Black", "3067327", "", 151.80, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T591800")
		CatalogDao.createProduct("Epson 11880", "700 ml Photo Black", "3067320", "", 151.80, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T591100")
		val p5 = CatalogDao.createProduct("Epson 11880", "700 ml Vivid Light Magenta", "3067325", "", 151.80, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T591600")
		val p6 = CatalogDao.createProduct("Epson 11880", "700 ml Vivid Magenta", "3067322", "", 151.80, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T591300")
		CatalogDao.createProduct("Epson 11880", "700 ml Yellow", "3067323", "", 151.80, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T591400")
		CatalogDao.createProduct("Epson 4800", "110 ml licht Magenta", "3083927", "", 32.19, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T605C00")
		CatalogDao.createProduct("Epson 4800", "110 ml Magenta", "3083926", "", 32.19, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T605B00")
		CatalogDao.createProduct("Epson 4880", "110 ml Cyan", "3067305", "", 32.19, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T605200")
		CatalogDao.createProduct("Epson 4880", "110 ml Light Black", "3067310", "", 32.19, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T605700")
		CatalogDao.createProduct("Epson 4880", "110 ml Light Cyan", "3067308", "", 32.19, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T605500")
		CatalogDao.createProduct("Epson 4880", "110 ml Light Light Black", "3067311", "", 32.19, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T605900")
		CatalogDao.createProduct("Epson 4880", "110 ml Photo Black", "3067302", "", 32.19, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T605100")
		val p2 = CatalogDao.createProduct("Epson 4880", "110 ml Vivid Light Magenta", "3067309", "", 32.19, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T605600")
		val p3 = CatalogDao.createProduct("Epson 4880", "110 ml Vivid Magenta", "3067306", "", 32.19, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T605300")
		val p4 = CatalogDao.createProduct("Epson 4880", "110 ml Yellow", "3067307", "", 32.19, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T605400")
		CatalogDao.createProduct("Epson 7880-9880", "220 ml Cyan", "3067313", "", 51.08, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T603200")
		CatalogDao.createProduct("Epson 7880-9880", "220 ml Light Black", "3067318", "", 51.08, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T603700")
		CatalogDao.createProduct("Epson 7880-9880", "220 ml Light Cyan", "3067316", "", 51.08, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T603500")
		CatalogDao.createProduct("Epson 7880-9880", "220 ml Light Light Black", "3067319", "", 51.08, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T603900")
		CatalogDao.createProduct("Epson 7880-9880", "220 ml Photo Black", "3067312", "", 51.08, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T603100")
		val p1 = CatalogDao.createProduct("Epson 7880-9880", "220 ml Vivid Magenta", "3067314", "", 51.08, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T603300")
		CatalogDao.createProduct("Epson 7880-9880", "220 ml Yellow", "3067315", "", 51.08, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T603400")
		CatalogDao.createProduct("Epson 7880-9880", "220ml Vivid LightMagenta", "3067317", "", 51.08, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T603600")
		CatalogDao.createProduct("Epson 9800", "220ml Light Magenta", "3084871", "", 51.08, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "T603C00")
		CatalogDao.createProduct("Epson 9800", "220ml Magenta", "3084957", "", 51.08, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "T603B00")
		CatalogDao.createProduct("Epson onderhoudstank", "", "3067786", "", 18.85, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C12C890191")
		CatalogDao.createProduct("Epson reservemes", "automatische afsnijder", "3067787", "", 70.43, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C12C815291")
		CatalogDao.createProduct("Epson Stylus 7450", "220 ml Cyan", "3070591", "", 51.08, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T612200")
		CatalogDao.createProduct("Epson Stylus 7450 ", "220 ml matt Black", "3084406", "", 51.08, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T612800")
		CatalogDao.createProduct("Epson Stylus 7450", "220 ml Photo Black", "3070590", "", 51.08, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T612100")
		CatalogDao.createProduct("Epson Stylus 7450", "220 ml Vivid Magenta", "3070592", "", 51.08, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T612300")
		CatalogDao.createProduct("Epson Stylus 7450", "220 ml Yellow", "3070593", "", 51.08, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T612400")
		CatalogDao.createProduct("Epson Stylus GS6000", "cleaning T623 950 ml", "3071706", "", 30.76, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T623000")
		CatalogDao.createProduct("Epson Stylus GS6000", "Cyan T624 950 ml", "3071707", "", 132.00, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T624200")
		CatalogDao.createProduct("Epson Stylus GS6000", "Green T624 950 ml", "3071708", "", 132.00, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T624700")
		CatalogDao.createProduct("Epson Stylus GS6000", "Ink Cleaner", "3071715", "", 52.00, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C12C890621")
		CatalogDao.createProduct("Epson Stylus GS6000", "Light Cyan T624 950", "3071709", "", 132.00, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T624500")
		CatalogDao.createProduct("Epson Stylus GS6000", "Magenta T624 950 ml", "3071711", "", 132.00, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T624300")
		CatalogDao.createProduct("Epson Stylus GS6000", "Maintenance Kit", "3071716", "", 39.00, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C12C890611")
		CatalogDao.createProduct("Epson Stylus GS6000", "Orange T624 950 ml", "3071712", "", 132.00, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T624800")
		CatalogDao.createProduct("Epson Stylus GS6000", "PhotoBlackT624 950ml", "3071713", "", 132.00, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T624100")
		CatalogDao.createProduct("Epson Stylus GS6000", "Yellow T624 950 ml", "3071714", "", 132.00, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T624400")
		CatalogDao.createProduct("Epson T544", "4000/7600/9600 220ml Cyan", "3083708", "", 51.08, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T544200")
		CatalogDao.createProduct("Epson T544", "4000/7600/9600 220ml L. Cyan", "3083710", "", 51.08, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T544500")
		CatalogDao.createProduct("Epson T544", "4000/7600/9600 220ml L.black", "3083709", "", 51.08, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T544700")
		CatalogDao.createProduct("Epson T544", "4000/7600/9600 220ml Magenta", "3083712", "", 51.08, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T544300")
		CatalogDao.createProduct("Epson T544", "4000/7600/9600 220ml Yellow", "3083715", "", 51.08, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T544400")
		CatalogDao.createProduct("Epson T544", "7600/9600 220ml Light Magenta", "3083711", "", 51.08, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T544600")
		CatalogDao.createProduct("Epson T544", "7600/9600 220ml Matte Black", "3083713", "", 51.08, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T544800")
		CatalogDao.createProduct("Epson T544", "7600/9600 220ml Photo Black", "3083714", "", 51.08, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T544100")
		CatalogDao.createProduct("Epson T596", "7900-9900 350ml Cyan", "3080234", "", 77.22, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T596200")
		CatalogDao.createProduct("Epson T596", "7900-9900 350ml Green", "3080235", "", 77.22, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T596B00")
		CatalogDao.createProduct("Epson T596", "7900-9900 350ml L.Light Black", "3080238", "", 77.22, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T596900")
		CatalogDao.createProduct("Epson T596", "7900-9900 350ml Light Black", "3080236", "", 77.22, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T596700")
		CatalogDao.createProduct("Epson T596", "7900-9900 350ml Light Cyan", "3080237", "", 77.22, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T596500")
		CatalogDao.createProduct("Epson T596", "7900-9900 350ml Matt Black", "3080239", "", 77.22, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T596800")
		CatalogDao.createProduct("Epson T596", "7900-9900 350ml Orange", "3080240", "", 77.22, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T596A00")
		CatalogDao.createProduct("Epson T596", "7900-9900 350ml Photo Black", "3080241", "", 77.22, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T596100")
		CatalogDao.createProduct("Epson T596", "7900-9900 350ml Vivid Light M", "3080242", "", 77.22, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T596600")
		CatalogDao.createProduct("Epson T596", "7900-9900 350ml Vivid Magenta", "3080243", "", 77.22, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T596300")
		CatalogDao.createProduct("Epson T596", "7900-9900 350ml Yellow", "3080244", "", 77.22, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T596400")
		CatalogDao.createProduct("EpsonStylus GS6000", "LightMagentaT624 950m", "3071710", "", 132.00, getClass, "epsonInk.jpg", List(epsonInks, colorInk), supplier = "Epson NL - Amsterdam", supplierArticleNumber = "C13T624600")
		
		CatalogDao.setRelated("supplies", epsonStylusPro4880, p1, p2, p3, p4, p5, p6)

			  new VolumeDiscountPromotion useIn { promotion =>
	  promotion.setProduct(p6)
	  promotion.setVolumeDiscount(3)
	  promotion.setPrice(7.55)
	  promotion.setStartDate(new java.util.Date())
      promotion.setEndDate(new java.util.Date(System.currentTimeMillis + 40 * 24 * 60 * 60 * 1000))
	  promotion.setShop(shop)
	  shop.getPromotions.add(promotion)
    }

  }
  
  def hpproducts = {
	  val hpInks = CatalogDao.getOrCreateCategory("Hewlett Packard Inks")
	  ink.getChildren.add(hpInks)
	  hp.getChildren.add(hpInks)

		CatalogDao.createProduct("HP 100 Cartridge", "C4836AE blauw","3040827","",21.73, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4836AE")
		CatalogDao.createProduct("HP 100 Cartridge", "C4837AE rood","3040828","",20.80, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4837AE")
		CatalogDao.createProduct("HP 100 Cartridge", "C4838AE geel","3042737","",22.97, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4838AE")
		CatalogDao.createProduct("HP 100 Cartridge", "C4844AE zwart","3036495","",22.08, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4844AE")
		CatalogDao.createProduct("HP 100 Printhead", "C4810A  zwart","3042736","",23.59, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4810A")
		CatalogDao.createProduct("HP 120 Cartridge", "C4913A geel","3040829","",21.58, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4913A")
		CatalogDao.createProduct("HP 120 Cartridge", "C5017A Licht blauw","3040831","",22.25, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C5017A")
		CatalogDao.createProduct("HP 120 Cartridge", "C5018A Licht rood","3040832","",22.25, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C5018A")
		CatalogDao.createProduct("HP 120 Printhead", "C4811A blauw","3040833","",23.59, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4811A")
		CatalogDao.createProduct("HP 120 Printhead", "C4812A rood","3040834","",23.59, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4812A")
		CatalogDao.createProduct("HP 120 Printhead", "C4813A geel","3040835","",23.59, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4813A")
		CatalogDao.createProduct("HP 120 Printhead", "C5020A licht blauw","3040837","",23.59, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C5020A")
		CatalogDao.createProduct("HP 120 Printhead", "C5021A licht rood","3040838","",23.59, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C5021A")
		CatalogDao.createProduct("HP 130 Cartridge", "C9425A blauw","3050484","",23.89, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C9425A")
		CatalogDao.createProduct("HP 130 Cartridge", "C9426A rood","3050485","",23.89, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C9426A")
		CatalogDao.createProduct("HP 130 Cartridge", "C9427A geel","3050486","",26.89, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C9427A")
		CatalogDao.createProduct("HP 130 Cartridge", "C9428A licht blauw","3050487","",26.89, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C9428A")
		CatalogDao.createProduct("HP 130 Cartridge", "C9429A licht rood","3050488","",26.89, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C9429A")
		CatalogDao.createProduct("HP 700 Cart+Printh","51644C blauw","3006369","",21.17, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "51644C")
		CatalogDao.createProduct("HP 700 Cart+Printh","51644M rood","3006370","",21.17, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "51644M")
		CatalogDao.createProduct("HP 700 Cart+Printh","51644Y geel","3006371","",21.17, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "51644Y")
		CatalogDao.createProduct("HP 700 Cart+Printh","51645A zwart","3006368","",20.38, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "51645A")
		CatalogDao.createProduct("HP1000 Cartridge", "C4846A blauw 350ml","3024729","",90.06, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4846A")
		CatalogDao.createProduct("HP1000 Cartridge", "C4847A rood 350ml","3024730","",90.06, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4847A")
		CatalogDao.createProduct("HP1000 Cartridge", "C4848A geel 350ml","3024731","",85.77, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4848A")
		CatalogDao.createProduct("HP1000 Cartridge", "C4871A zwart 350ml","3024728","",90.06, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4871A")
		CatalogDao.createProduct("HP1000 Cartridge", "C4872A blauw 175ml","3026802","",54.35, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4872A")
		CatalogDao.createProduct("HP1000 Cartridge", "C4873A geel 175ml","3026803","",54.35, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4873A")
		CatalogDao.createProduct("HP1000 Cartridge", "C4874A rood 175ml","3026805","",54.35, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4874A")
		CatalogDao.createProduct("HP1000 Printh& Cleaner", "C4820A zwart","3026806","",95.87, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4820A")
		CatalogDao.createProduct("HP1000 Printh& Cleaner", "C4821A blauw","3026807","",80.09, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4821A")
		CatalogDao.createProduct("HP1000 Printh& Cleaner", "C4822A rood","3026809","",95.87, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4822A")
		CatalogDao.createProduct("HP1000 Printh& Cleaner", "C4823A geel","3026808","",80.09, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4823A")
		CatalogDao.createProduct("HP120 / HP130 Cartridge", "C5016A zwart","3050483","",26.2, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C5016A")
		CatalogDao.createProduct("HP5000 Cartridge", "C4930A  zwart","3032933","",124.11, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4930A")
		CatalogDao.createProduct("HP5000 Cartridge", "C4931A blauw","3032935","",124.11, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4931A")
		CatalogDao.createProduct("HP5000 Cartridge", "C4932A rood","3032936","",124.11, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4932A")
		CatalogDao.createProduct("HP5000 Cartridge", "C4933A geel","3032937","",124.11, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4933A")
		CatalogDao.createProduct("HP5000 Cartridge", "C4934A licht blauw","3032938","",124.11, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4934A")
		CatalogDao.createProduct("HP5000 Cartridge", "C4935A licht rood","3032939","",124.11, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4935A")
		CatalogDao.createProduct("HP5000 Printh+Cleaner", "C4950A zwart","3040425","",85.33, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4950A")
		CatalogDao.createProduct("HP5000 Printh+Cleaner", "C4951A blauw","3040426","",85.33, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4951A")
		CatalogDao.createProduct("HP5000 Printh+Cleaner", "C4952A rood","3040427","",85.33, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4952A")
		CatalogDao.createProduct("HP5000 Printh+Cleaner", "C4953A geel","3040428","",85.33, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4953A")
		CatalogDao.createProduct("HP5000 Printh+Cleaner", "C4954A licht blauw","3040355","",92.94, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4954A")
		CatalogDao.createProduct("HP5000 Printh+Cleaner", "C4955A licht rood","3043729","",85.33, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4955A")
		CatalogDao.createProduct("HP70 130ml", "Blue Vivera Ink cartridge","3065703","",40.89, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C9458A")
		CatalogDao.createProduct("HP70 130ml", "Cyan Vivera Ink cartridge","3065698","",45.08, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C9452A")
		CatalogDao.createProduct("HP70 130ml", "Gloss Enhancer Ink cartridge","3065704","",30.55, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C9459A")
		CatalogDao.createProduct("HP70 130ml", "Green Vivera Ink cartridge","3065702","",40.89, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C9457A")
		CatalogDao.createProduct("HP70 130ml", "Grey Vivera Ink cartridge","3065696","",40.89, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C9450A")
		CatalogDao.createProduct("HP70 130ml", "Light Cyan (Vivera Ink)","3066080","",45.07, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C9390A")
		CatalogDao.createProduct("HP70 130ml", "Light Grey Vivera Ink cartr","3065697","",45.07, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C9451A")
		CatalogDao.createProduct("HP70 130ml", "Light Magenta Vivera Ink cart","3065693","",45.07, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C9455A")
		CatalogDao.createProduct("HP70 130ml", "Magenta Vivera Ink cartridge","3065699","",45.07, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C9453A")
		CatalogDao.createProduct("HP70 130ml", "Matte Black Vivera Ink cart","3065694","",45.07, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C9448A")
		CatalogDao.createProduct("HP70 130ml", "Photo Black Vivera Ink cart","3065695","",45.07, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C9449A")
		CatalogDao.createProduct("HP70 130ml", "Red Vivera Ink cartridge","3065701","",40.89, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C9456A")
		CatalogDao.createProduct("HP70 130ml", "Yellow Vivera Ink cartridge","3065700","",45.07, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C9454A")
		CatalogDao.createProduct("Inktcartr Dye", "blauw 2000/2500/3000/3500","3017158","",101.55, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C1807A")
		CatalogDao.createProduct("Inktcartr Dye", "geel 2000/2500/3000/3500","3017160","",101.56, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C1809A")
		CatalogDao.createProduct("Inktcartr Dye", "rood 2000/2500/3000/3500","3017159","",101.55, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C1808A")
		CatalogDao.createProduct("Inktcartr Dye", "zwart 2000/2500/3000/3500","3017157","",101.55, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C1806A")
		CatalogDao.createProduct("Inktcartridge", "blauw nr10 28ml","3036491","",22.97, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4841AE")
		CatalogDao.createProduct("Inktcartridge", "geel nr10 28ml","3036493","",22.97, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4842AE")
		CatalogDao.createProduct("Inktcartridge", "rood nr10 28ml","3036492","",22.97, getClass, "hpink.jpg", List(hpInks, colorInk), supplier= "Five 4 U", supplierArticleNumber = "C4843AE")
  	
  }
}