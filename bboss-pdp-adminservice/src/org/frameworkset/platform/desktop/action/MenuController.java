package org.frameworkset.platform.desktop.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.frameworkset.platform.common.JSTreeNode;
import org.frameworkset.platform.common.TreeNodeStage;
import org.frameworkset.platform.common.ZKTreeNode;
import org.frameworkset.platform.config.model.ResourceInfo;
import org.frameworkset.platform.framework.Framework;
import org.frameworkset.platform.framework.Item;
import org.frameworkset.platform.framework.MenuItem;
import org.frameworkset.platform.framework.MenuQueue;
import org.frameworkset.platform.framework.Module;
import org.frameworkset.platform.framework.SubSystem;
import org.frameworkset.platform.resource.ResourceManager;
import org.frameworkset.platform.security.AccessControl;
import org.frameworkset.platform.security.AuthorResource;
import org.frameworkset.platform.security.authorization.AuthRole;
import org.frameworkset.platform.util.AdminUtil;
import org.frameworkset.util.annotations.PagerParam;
import org.frameworkset.util.annotations.ResponseBody;
import org.frameworkset.web.servlet.ModelMap;

import com.frameworkset.common.poolman.Record;
import com.frameworkset.common.poolman.handle.RowHandler;
import com.frameworkset.platform.admin.entity.MenuOPS;
import com.frameworkset.platform.admin.entity.Role;
import com.frameworkset.platform.admin.service.RoleService;
import com.frameworkset.util.ListInfo;

public class MenuController{
	private ResourceManager resourceManager = new ResourceManager();
	private RoleService roleService;
	public String index(ModelMap model){
		 
		return "path:main";
	}
	public String grantedcolumns(String resourceType,String roleId,String roleType,ModelMap model){
		if(resourceType == null)
			resourceType = "column";
		String currentSystem = AccessControl.getAccessControl().getCurrentSystemID();
		final Framework framework = Framework.getInstance(currentSystem);		
		model.addAttribute("roleId", roleId);
		model.addAttribute("roleType", roleType);
		model.addAttribute("resourceType", resourceType);
		ResourceInfo resourceInfo = resourceManager.getResourceInfoByType(resourceType);
		if(roleType.equals(AuthRole.TYPE_ROLE)){
			Role role = null;
			  role = this.roleService.getRole(roleId);				
			model.addAttribute("roleNeedGrantResource", AdminUtil.roleNeedGrantResource(role.getRoleName()));
				
		}
		else
		{
			model.addAttribute("roleNeedGrantResource", true);
		}
		
		return "path:grantedcolumns";
	}
	public String grantedcolumnsListInfo(String resourceType,String roleId,String roleType, 
			String resourceAttr,
			@PagerParam(name = PagerParam.OFFSET) long offset,
			@PagerParam(name = PagerParam.PAGE_SIZE, defaultvalue = "10") int pagesize,ModelMap model){
		if(resourceType == null)
			resourceType = "column";
		String currentSystem = AccessControl.getAccessControl().getCurrentSystemID();
		final Framework framework = Framework.getInstance(currentSystem);		
		model.addAttribute("roleId", roleId);
		model.addAttribute("roleType", roleType);
		model.addAttribute("resourceType", resourceType);
		ResourceInfo resourceInfo = resourceManager.getResourceInfoByType(resourceType);
		
		@SuppressWarnings("unchecked")
		ListInfo grantedcolumns = this.roleService.getGrantedOperations("visible",resourceType, roleId, roleType,  resourceAttr,
				   offset,
					  pagesize, resourceInfo.getPermissionTable(), new RowHandler<MenuOPS>(){
			
			@Override
			public void handleRow(MenuOPS menu,Record origine) throws Exception {
				String op_id = origine.getString("op_id");
				menu.setOpcode(op_id);
				menu.setResCode(origine.getString("RES_ID"));
				menu.setResName(origine.getString("RES_NAME"));
				menu.setResourceType(origine.getString("RESTYPE_ID"));					
				menu.setAUTHORIZATION_ETIME(origine.getDate("AUTHORIZATION_ETIME"));
				menu.setAUTHORIZATION_STIME(origine.getDate("AUTHORIZATION_STIME"));
				MenuItem menuItem = framework.getMenuByID(menu.getResCode());
				if(menuItem != null)
				{
					menu.setMenuName(menuItem.getName());
					menu.setMenupath(menuItem.getPath());
					AuthorResource ar = (AuthorResource)menuItem;
					menu.setUrls(ar.toString("<br>"));
				}
			}
			
		},MenuOPS.class);
		model.addAttribute("grantedcolumns", grantedcolumns);
		return "path:grantedcolumnsListInfo";
	}
	
	private ZKTreeNode buildZKTreeNode(MenuItem menu)
	{
		ZKTreeNode JSTreeNode = new ZKTreeNode();
		JSTreeNode.setId(menu.getId());
//		JSTreeNode.setText(new StringBuilder().append("<a href=\"#\" onclick=\"javascript:Sysmanager.showOrgUsers('").append(org.getOrgId()).append("');\">").append(org.getOrgName()).append("</a>").toString());
		JSTreeNode.setName(menu.getName());
		JSTreeNode.setIsParent(true);
		return JSTreeNode;
	}
	private JSTreeNode buildJSTreeNode(String systemid,MenuItem menu)
	{
		JSTreeNode JSTreeNode = new JSTreeNode();
		JSTreeNode.setId(systemid+":"+menu.getId());
//		JSTreeNode.setText(new StringBuilder().append("<a href=\"#\" onclick=\"javascript:Sysmanager.showOrgUsers('").append(org.getOrgId()).append("');\">").append(org.getOrgName()).append("</a>").toString());
		JSTreeNode.setText(menu.getName());
		if(menu instanceof Item)
			JSTreeNode.setType("item");
		else
			JSTreeNode.setType("module");
		JSTreeNode.setIcon(null);
		TreeNodeStage state = new TreeNodeStage();
		state.setDisabled(false);
		state.setOpened(false);
		state.setSelected(false);
		JSTreeNode.setState(state);
		JSTreeNode.setChildren(true);
		return JSTreeNode;
	}
	public @ResponseBody List<JSTreeNode> getChildrens(String parent){
		String currentSystem = AccessControl.getAccessControl().getCurrentSystemID();
		
		if(currentSystem.equals("module")){
			
			if(parent == null || parent.equals("#")){		
				Map<String,SubSystem> systems = Framework.getInstance().getSubsystems();
				List<JSTreeNode> treeNodes = new ArrayList<JSTreeNode>();
				Framework framework = Framework.getInstance(currentSystem);
				
				JSTreeNode JSTreeNode = new JSTreeNode();
				JSTreeNode.setId("system:"+framework.getSystemid());
				 
//				JSTreeNode.setText(new StringBuilder().append("<a href=\"#\" onclick=\"javascript:Sysmanager.showOrgUsers('").append(org.getOrgId()).append("');\">").append(org.getOrgName()).append("</a>").toString());
				JSTreeNode.setText(framework.getDescription());
				
				JSTreeNode.setType("system");
				TreeNodeStage state = new TreeNodeStage();
				state.setDisabled(false);
				state.setOpened(true);
				state.setSelected(false);
				JSTreeNode.setState(state);
				JSTreeNode.setChildren(true);
				treeNodes.add(JSTreeNode);
				if(systems != null && systems.size() > 0){
					Set<Entry<String, SubSystem>> entries = systems.entrySet();
					for(Entry<String, SubSystem> entry:entries){
						SubSystem sys = entry.getValue();
						JSTreeNode = new JSTreeNode();
						JSTreeNode.setId("system:"+sys.getId());
						 
//						JSTreeNode.setText(new StringBuilder().append("<a href=\"#\" onclick=\"javascript:Sysmanager.showOrgUsers('").append(org.getOrgId()).append("');\">").append(org.getOrgName()).append("</a>").toString());
						JSTreeNode.setText(sys.getName());
						JSTreeNode.setType("system");
						 state = new TreeNodeStage();
						state.setDisabled(false);
						state.setOpened(false);
						state.setSelected(false);
						JSTreeNode.setState(state);
						JSTreeNode.setChildren(true);
						treeNodes.add(JSTreeNode);
					}
				}
				return treeNodes;
			}
			else
			{
				String[] idinfos = parent.split(":");
				if(idinfos[0].equals("system")){
					Framework framework = Framework.getInstance(idinfos[1]);
					MenuQueue menus = framework.getMenus();
					if(menus != null && menus.size() > 0){
						List<JSTreeNode> treeNodes = new ArrayList<JSTreeNode>();
						for(MenuItem menu:menus.getList())
						{
							treeNodes.add(this.buildJSTreeNode(idinfos[1],menu));
						}
						return treeNodes;
					}
				}
				else
				{
					Framework framework = Framework.getInstance(idinfos[0]);
					MenuItem menuItem = framework.getMenuByID(idinfos[1]);
					if(menuItem instanceof Module){
						MenuQueue menus = ((Module)menuItem).getMenus();
						if(menus != null && menus.size() > 0){
							List<JSTreeNode> treeNodes = new ArrayList<JSTreeNode>();
							for(MenuItem menu:menus.getList())
							{
								treeNodes.add(this.buildJSTreeNode(idinfos[0],menu));
							}
							return treeNodes;
						}
					}
				}
					
			
				
			}
		}
		else
		{
			Framework framework = Framework.getInstance(currentSystem);
			if(parent == null || parent.equals("#")){
				
				MenuQueue menus = framework.getMenus();
				if(menus != null && menus.size() > 0){
					List<JSTreeNode> treeNodes = new ArrayList<JSTreeNode>();
					for(MenuItem menu:menus.getList())
					{
						treeNodes.add(this.buildJSTreeNode(currentSystem,menu));
					}
					return treeNodes;
				}
					
				
			}
			else
			{
				String[] idinfos = parent.split(":");
				MenuItem menuItem = framework.getMenuByID(idinfos[1]);
				if(menuItem instanceof Module){
					MenuQueue menus = ((Module)menuItem).getMenus();
					if(menus != null && menus.size() > 0){
						List<JSTreeNode> treeNodes = new ArrayList<JSTreeNode>();
						for(MenuItem menu:menus.getList())
						{
							treeNodes.add(this.buildJSTreeNode(currentSystem,menu));
						}
						return treeNodes;
					}
				}
				
			}
		}
		
		 
		return null;
		 
			
	}
	
	public @ResponseBody List<ZKTreeNode> getzkChildrens(String id,String roleId,String roleType,ModelMap model){
		String currentSystem = AccessControl.getAccessControl().getCurrentSystemID();
		model.addAttribute("roleId", roleId);
		model.addAttribute("roleType", roleType);
		 
		Framework framework = Framework.getInstance(currentSystem);
		if(id == null || id.equals("#")){
			MenuQueue menus = framework.getMenus();
			if(menus != null && menus.size() > 0){
				List<ZKTreeNode> treeNodes = new ArrayList<ZKTreeNode>();
				for(MenuItem menu:menus.getList())
				{
					treeNodes.add(this.buildZKTreeNode(menu));
				}
				return treeNodes;
			}
				
			
		}
		else
		{
			
			MenuItem menuItem = framework.getMenuByID(id);
			if(menuItem instanceof Module){
				MenuQueue menus = ((Module)menuItem).getMenus();
				if(menus != null && menus.size() > 0){
					List<ZKTreeNode> treeNodes = new ArrayList<ZKTreeNode>();
					for(MenuItem menu:menus.getList())
					{
						treeNodes.add(this.buildZKTreeNode(menu));
					}
					return treeNodes;
				}
			}
			
		}
		 
		return null;
		 
			
	}
	public String columnAuthTree(String roleId,String roleType,ModelMap model){
		model.addAttribute("roleId", roleId);
		model.addAttribute("roleType", roleType);
		return "path:columnAuthTree";
	}
	
	public String system(String systemid,ModelMap model){
		Framework framework = Framework.getInstance(systemid);
		model.addAttribute("systemid", systemid);
		model.addAttribute("systemname", framework.getDescription());
		model.addAttribute("framework", framework);
		model.addAttribute("publicItem", framework.getPublicItem());
		model.addAttribute("subSystems", framework.getSubsystems());
		if(framework.getRootsystem() != null)
			model.addAttribute("rootsystem", framework.getRootsystem());
		else
			model.addAttribute("rootsystem", framework.getFrameworkmeta());
		
		return "path:system";
	}
	public String menuitem(String systemid,String menuid,ModelMap model){
		Framework framework = Framework.getInstance(systemid);
		model.addAttribute("systemid", systemid);
		model.addAttribute("systemname", framework.getDescription());
		model.addAttribute("menuid", menuid);
		
		model.addAttribute("framework", framework);
		MenuItem menu = framework.getMenuByID(menuid); 
		model.addAttribute("menu",   menu);
		model.addAttribute("menuname", menu.getName());
		if (menu instanceof Item) {
			model.addAttribute("menuType", "item");
		} else if (menu instanceof Module) {
			model.addAttribute("menuType", "module");
		}
		return "path:menuitem";
	}


}
