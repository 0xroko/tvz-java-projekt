package com.r.projektnizad.models;

import com.r.projektnizad.controllers.HistoryController;
import com.r.projektnizad.controllers.category.ViewCategoryController;
import com.r.projektnizad.controllers.item.ViewItemController;
import com.r.projektnizad.controllers.order.ViewOrderCategory;
import com.r.projektnizad.controllers.order.ViewOrderItemsDialog;
import com.r.projektnizad.controllers.table.ViewTableController;

public sealed interface CleanableScene permits HistoryController, ViewCategoryController, ViewItemController, ViewOrderCategory, ViewOrderItemsDialog, ViewTableController {
  void cleanup();
}
